import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class bresenhamLine extends java.applet.Applet implements MouseListener, MouseMotionListener {

    private static final long serialVersionUID = 1L;
    int width, height;
    int x1 = 0, y1 = 0, x2 = 0, y2 = 0, pixelsize = 10;
    public void init() {
        width = getSize().width;
        height = getSize().height;
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

    }

    // рисуем линию из x1,y1 в x2,y2
    public void bresenham_line(int x1, int y1, int x2, int y2) {
        // если точка x1, y1 is справа от x2, y2, возвращяем\меняем их
        if ((x1 - x2) > 0) {bresenham_line(x2, y2, x1, y1); return;}
        // инициализируем линию
        // function Math.abs(y) абсолютное y
        if (Math.abs(y2 - y1) > Math.abs(x2 - x1)) {
            // строка и угол оси y меньше чем 45 градусов
            // значит скачем к сл. процедуре
            bresteepline(y1, x1, y2, x2); return;
        }
        // линии и x составляют менее 45 градусов, и х направляющаяя
        // auxiliary variables
        int x = x1, y = y1, sum = x2 - x1, Dx = 2 * (x2 - x1), Dy = Math.abs(2 * (y2 - y1));
        int prirastokDy = ((y2 - y1) > 0) ? 1 : -1;
        // рисуем линию
        for (int i = 0; i <= x2 -x1; i++) {
            setpix(x, y);
            x++;
            sum -= Dy;
            if (sum < 0) {y = y + prirastokDy; sum += Dx;}
        }
    }

    public void bresteepline(int x3, int y3, int x4, int y4) {
        // если точка x3, y3 правее токи x4, y4, меняем их
        if ((x3 - x4) > 0) {bresteepline(x4, y4, x3, y3); return;}

        int x = x3, y = y3, sum = x4 - x3, Dx = 2 * (x4 - x3), Dy = Math.abs(2 * (y4 - y3));
        int prirastokDy = ((y4 - y3) > 0) ? 1 : -1;

        for (int i = 0; i <= x4 -x3; i++) {
            setpix(y, x);
            x++;
            sum -= Dy;
            if (sum < 0) {y = y + prirastokDy; sum += Dx;}
        }
    }

    public void setpix(int x, int y)
    {
        Graphics g =getGraphics();
        g.setColor(Color.blue);
        g.fillRect(pixelsize * x, pixelsize * y, pixelsize, pixelsize);
    }
    public void paint( Graphics g )
    {
        Dimension d = getSize();
        //сетка
        for (int ix=0, iy=0; ix<=getSize().width; ix+=pixelsize)
        {
            g.drawLine(ix,0, ix, getSize().height);
            g.drawLine(0,iy, getSize().width, iy);
            iy+=pixelsize;
        }
        //оси
        g.setColor(Color.RED);
        g.drawLine(0, (getSize().height)/2, getWidth(), getHeight()/2);
        g.drawLine(getWidth()/2, 0, getWidth()/2, getHeight());
        g.setColor(Color.BLACK);

        g.drawLine(0,0,d.width,0);
        g.drawLine(0,0,0,d.height);
        g.drawLine(d.width-1,d.height-1,d.width-1,0);
        g.drawLine(d.width-1,d.height-1,0,d.height-1);
        bresenham_line(x1, y1, x2, y2);
    }
    public void mousePressed(MouseEvent e) // обработчик нажатия
    {
        x1 = e.getX()/pixelsize;
        y1 = e.getY()/pixelsize;
    }
    public void mouseDragged(MouseEvent e) // отпускаем
    {
        x2 = e.getX()/pixelsize;
        y2 = e.getY()/pixelsize;
        repaint();
    }

    public void mouseReleased(MouseEvent e) {;}
    public void mouseClicked(MouseEvent e) {;}
    public void mouseEntered(MouseEvent e) {;}
    public void mouseExited(MouseEvent e) {;}
    public void mouseMoved(MouseEvent e) {;}

}
