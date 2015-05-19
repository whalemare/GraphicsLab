import java.applet.Applet;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;


public class BresenhamCircle extends Applet implements  AdjustmentListener {

    private static final long serialVersionUID = 1L;

    int y0 = 0;
    int x0 = 0;
    int d = 5;
    int r = 0;
    Label Hlabel = new Label();
    Scrollbar Hrubka = new Scrollbar(Scrollbar.HORIZONTAL, 5, 1, 1, 100);
    Color Farba = new Color(0,0,0);
    Checkbox Vymaz = new Checkbox("Очистить экран");
    Graphics Buffer;
    Dimension size1 = getSize();
    private Image imgBuffer;

    public void init() {
        d = Hrubka.getValue();

        add(Hlabel);
        add(Hrubka);
        add(Vymaz);

        Hlabel.setText("Ширина px: "+Hrubka.getValue());

        Vymaz.setState(true);
        Hrubka.addAdjustmentListener(this);
        resize(600,400);

        size1 = getSize();
        imgBuffer = createImage(size1.width, size1.height);
        Buffer = imgBuffer.getGraphics();

        y0 = getHeight()/2;
        x0 = getWidth()/2;
        r = getHeight() / 3;

        super.init();
    }


    public void start() {

        super.start();
    }

    public void stop() {
        super.stop();
    }

    public void destroy() {
        super.destroy();
    }

    public void adjustmentValueChanged(AdjustmentEvent e){
        if(e.getAdjustable().equals(Hrubka)){
            d = Hrubka.getValue();
            Hlabel.setText("Ширина px: "+Hrubka.getValue());
        }
        repaint();
    }

    public boolean action (Event evt, Object arg) {
        int r = 0, g = 0, b = 0;/*
        if (R.getSelectedItem() != "R") {
            r = Integer.parseInt(R.getSelectedItem());
            //	System.out.println(R.getSelectedItem());
        }
        if (G.getSelectedItem() != "G") {
            g = Integer.parseInt(G.getSelectedItem());
            //	System.out.println(G.getSelectedItem());
        }
        if (B.getSelectedItem() != "B") {
            b = Integer.parseInt(B.getSelectedItem());
            //	System.out.println(B.getSelectedItem());
        }*/
        Farba = new Color(r,g,b);
        repaint();
        return true;

    }

    public boolean mouseDrag(Event arg0, int x, int y) {

        r = (int)Math.round(Math.sqrt(Math.pow(x0 - x,2)+ Math.pow(y0 - y,2)));

        repaint();
        return super.mouseDrag(arg0, x, y);
    }

    public boolean mouseDown(Event arg0, int x, int y) {
        d = Hrubka.getValue();
        x0 = x;
        y0 = y;

        repaint();
        return super.mouseDown(arg0, x, y);
    }


    public void setPixel(int x, int y, int d, Graphics g) {
        g.setColor(Farba);
        g.fillRect(x - (d/2), y - (d/2), d, d);
    }

    public void update(Graphics g) {
        paint(g);
    }

    public void paint( Graphics g ) {

        int rr = r - (r % d);
        int y = rr;

        if((size1.width != getWidth()) || (size1.height != getHeight())){
            size1.width = getWidth();
            size1.height = getHeight();
            imgBuffer = createImage(getWidth(),getHeight());
            Buffer = imgBuffer.getGraphics();
        }
        if(Vymaz.getState()){
            Buffer.setColor(Color.WHITE);
            Buffer.fillRect(0,0,getWidth(),getWidth());
        }
        for(int x = 0; x <= rr/Math.sqrt(2)+1; x+=d){
            y = (int)Math.round(Math.sqrt(Math.pow(rr,2)-Math.pow(x,2)));
            y = (y/d)*d;

            setPixel(x0 + x, y0 + y, d, imgBuffer.getGraphics());
            setPixel(x0 - x, y0 + y, d, imgBuffer.getGraphics());
            setPixel(x0 + x, y0 - y, d, imgBuffer.getGraphics());
            setPixel(x0 - x, y0 - y, d, imgBuffer.getGraphics());
            setPixel(x0 + y, y0 + x, d, imgBuffer.getGraphics());
            setPixel(x0 - y, y0 + x, d, imgBuffer.getGraphics());
            setPixel(x0 + y, y0 - x, d, imgBuffer.getGraphics());
            setPixel(x0 - y, y0 - x, d, imgBuffer.getGraphics());
        }



        g.drawImage(imgBuffer,0,0, this);
/* cетка, не вывожу потму что хуево работает а прикручивать правильно мне в ломы, т.к. не сделал этого в начале
        for (int ix=0, iy=0; ix<=getSize().width; ix+=d)
        {
            g.drawLine(ix,0, ix, getSize().height);
            g.drawLine(0,iy, getSize().width, iy);
            iy+=d;
*/
        }
    }
