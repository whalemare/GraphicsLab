import java.applet.*;
import java.awt.*;
import java.util.*;

public class FillPoly extends Applet {
    private Panel tlacidlo;
    private Button zmaz, vyplnFar;
    private Choice vyberfarby;
    private FillPolyCanvas vypln;
    private final static String farbyS[] = {
            "Рандомный цвет"
    };

    public void init () {
        setLayout(new BorderLayout());
        tlacidlo = new Panel();
        tlacidlo.add(zmaz = new Button("Очистить"));
        tlacidlo.add(vyplnFar = new Button("Залить"));
        vyberfarby = new Choice();
        for (int i = 0; i < farbyS.length; i++)
            vyberfarby.addItem(farbyS[i]);
        tlacidlo.add(vyberfarby);
        add("North", tlacidlo);
        vypln = new FillPolyCanvas();
        vypln.resize(size().width, size().height - 40);
        add("South", vypln);
        vypln.init();
        setBackground(new Color(0, 0, 0x80));
        setForeground(Color.white);
    }

    public boolean action (Event ev, Object obj) {
        if (vypln.mode == FillPolyCanvas.NIC)
            return true;
        if (ev.target == zmaz)
            vypln.clearAll();
        else if (ev.target == vyplnFar)
            vypln.picpick();
        else if (ev.target == vyberfarby)
            vypln.cisloFarby = vyberfarby.getSelectedIndex();
        return true;
    }

}

class FillPolyCanvas extends Canvas {
    private final static int MaxPoc = 64;
    private vrchol ps[] = new vrchol[MaxPoc];
    private Info es[] = new Info[MaxPoc];
    private int np, ne;

    final static int KRESLI = 0, FILL = 2, NIC = 3;
    int mode, cisloFarby;

    private final static Color farby[] = {
            Color.white, Color.red, Color.green, Color.yellow,
            Color.blue, Color.magenta, Color.cyan, Color.orange,
            Color.pink, Color.white, Color.lightGray, Color.gray,
            Color.darkGray, Color.black
    };
    private final static int NCOL = farby.length;


    void init () {
        mode = KRESLI;
        np = ne = cisloFarby = 0;
    }

    public boolean mouseDown (Event ev, int x, int y) {
        if (mode == KRESLI) {
            addPoint(x, y);
            repaint();
        }
        return true;
    }


    public void paint (Graphics g) {
        if (mode == KRESLI) {
            g.setColor(Color.white);
            g.drawString("KRESLI: <" + np + ">", 0, size().height - 5);
            for (int i = 0; i < np; i++) {
                g.setColor(new Color(ps[i].r, ps[i].g, ps[i].b));
                g.fillOval(ps[i].x - 3, ps[i].y - 3, 7, 7);
            }
            if (np == 0)
                return;
            g.setColor(Color.white);
            g.drawOval(ps[np-1].x - 3, ps[np-1].y - 3, 7, 7);
            g.setColor(Color.gray);
            g.drawLine(ps[np-1].x, ps[np-1].y, ps[0].x, ps[0].y);
            g.setColor(Color.cyan);
            for (int i = 1; i < np; i++)
                g.drawLine(ps[i-1].x, ps[i-1].y, ps[i].x, ps[i].y);

        } else if (mode != NIC) {
            g.setColor(Color.white);
            g.drawString("Fill: <" + ne + ">", 0, size().height - 5);
            scan(g);
        }
    }

    void addPoint (int x, int y) {
        if (np == MaxPoc)
            return;
        int c = (cisloFarby == 0) ? (int)(Math.random() * NCOL) : cisloFarby;
        ps[np++] = new vrchol(x, y, farby[c]);
    }

    void picpick () {
        if (np >= 3) {
            if (mode == KRESLI) {
                mode = NIC;
                makeTable();
            }
            mode = FILL;
            repaint();
        }
    }

    void clearAll () {
        for (int i = 0; i < np; i++)
            ps[i] = null;
        for (int i = 0; i < ne; i++)
            es[i] = null;
        np = ne = 0; mode = KRESLI;
        repaint();
    }

    void makeTable () {
        for (int i = 1; i < np; i++)
            if (ps[i-1].y != ps[i].y)
                es[ne++] = new Info(ps[i-1], ps[i]);
        es[ne++] = new Info(ps[np-1], ps[0]);
        Info tmp;
        for (int i = 0; i < ne - 1; i++)
            for (int j = i + 1; j < ne; j++)
                if (es[i].y0 > es[j].y0) {
                    tmp = es[i]; es[i] = es[j]; es[j] = tmp;
                }
    }

    void scan (Graphics g) {
        Vector act = new Vector();
        ActInfo ae;
        int y = es[0].y0;
        int eidx = 0;
        int ymax = size().height;
        g.setColor(farby[cisloFarby]);
        while (true) {
            Vector act1 = new Vector();
            for (int i = 0; i < act.size(); i++) {
                ae = (ActInfo)act.elementAt(i);
                if (ae.ym != y) {
                    ae.x += ae.dx;
                    if (mode == FILL) {
                        ae.r += ae.dr; ae.g += ae.dg; ae.b += ae.db;
                    }
                    addEdge(act1, ae);
                }
            }
            while (eidx < ne && es[eidx].y0 == y)
                addEdge(act1, new ActInfo(es[eidx++]));
            if (act1.size() == 0 || y == ymax)
                break;
            act = act1;
            if (mode == FILL)
                scanLineColor(y, act, g);
            else
                scanLine(y, act, g);
            y++;
        }
    }

    static void addEdge (Vector act, ActInfo ae) {
        int i;
        for (i = 0; i < act.size(); i++) {
            if (ae.x < ((ActInfo)act.elementAt(i)).x)
                break;
        }
        act.insertElementAt(ae, i);
    }

    void scanLine (int y, Vector act, Graphics g) {
        ActInfo ae1, ae2;
        int siz = act.size();
        for (int i = 0; i < siz; i += 2) {
            ae1 = (ActInfo)act.elementAt(i);
            ae2 = (ActInfo)act.elementAt(i + 1);
            g.drawLine((int)(ae1.x), y, (int)(ae2.x), y);
        }
    }

    void scanLineColor (int y, Vector act, Graphics g) {
        ActInfo ae1, ae2;
        double r1, g1, b1, dr, dg, db, xdif;
        int siz = act.size();
        for (int i = 0; i < siz; i += 2) {
            ae1 = (ActInfo)act.elementAt(i);
            ae2 = (ActInfo)act.elementAt(i + 1);
            xdif = (double)((int)ae2.x - (int)ae1.x);
            r1 = ae1.r; g1 = ae1.g; b1 = ae1.b;
            dr = (ae2.r - ae1.r) / xdif;
            dg = (ae2.g - ae1.g) / xdif;
            db = (ae2.b - ae1.b) / xdif;
            for (int x = (int)(ae1.x); x <= (int)(ae2.x); x++) {
                try {
                    g.setColor(new Color((int)r1, (int)g1, (int)b1));
                } catch (Exception e) { }
                g.drawLine(x, y, x, y);
                r1 += dr; g1 += dg; b1 += db;
            }
        }
    }
}

class vrchol {
    int x, y, r, g, b;
    vrchol (int x1, int y1, Color c) {
        x = x1; y = y1;
        r = c.getRed(); g = c.getGreen(); b = c.getBlue();
    }
    void move (int x1, int y1) {
        x = x1; y = y1;
    }
}

class Info {
    int x0, y0, y1;
    int r0, g0, b0;
    double dx, dr, dg, db;
    Info (vrchol p1, vrchol p2) {
        if (p1.y < p2.y) {
            x0 = p1.x; y0 = p1.y; y1 = p2.y;
            r0 = p1.r; g0 = p1.g; b0 = p1.b;
        } else {
            x0 = p2.x; y0 = p2.y; y1 = p1.y;
            r0 = p2.r; g0 = p2.g; b0 = p2.b;
        }
        dx = (double)(p1.x - p2.x) / (double)(p1.y - p2.y);
        dr = (double)(p1.r - p2.r) / (double)(p1.y - p2.y);
        dg = (double)(p1.g - p2.g) / (double)(p1.y - p2.y);
        db = (double)(p1.b - p2.b) / (double)(p1.y - p2.y);
    }
}

class ActInfo {
    int ym;
    double x, r, g, b;
    double dx, dr, dg, db;
    ActInfo (Info e) {
        x = (double)e.x0;
        r = (double)e.r0;
        g = (double)e.g0;
        b = (double)e.b0;
        dx = e.dx; ym = e.y1;
        dr = e.dr; dg = e.dg; db = e.db;
    }
}