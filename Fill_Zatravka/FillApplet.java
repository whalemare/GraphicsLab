import java.awt.*;
import java.applet.*;
import java.awt.event.*;



public class FillApplet extends Applet implements MouseListener,ActionListener,Runnable {

    FloodFill FF;

    Thread Timer;
    boolean done = true;
    int Alg;

    Font bigFont;
    Color FillColor;
    Color MenuBgColor;

    int xpos;			//позиция мыши х
    int ypos;			//позиция мыши y


    Button ButtonReset;

    CheckboxGroup radioGroup;
    Checkbox w4,w8,rec;

    CheckboxGroup AnimSpeedGroup;
    Checkbox slow,med,fast;


    int CanvasSizeX,CanvasSizeY;	// канва



    Graphics bufferGraphics;
    Image offscreen;
    Dimension dim;

    int CellSize;			//РАЗМЕР ОДНОЙ КЛЕТКИ

    int GetSpeed () {
        if (slow.getState()) return 750;
        else if (med.getState()) return 300;
        else return 100;

    }

    void DrawGrid (Graphics g) {					//Сетка

        int VLines = CanvasSizeX/CellSize;			///!///
        int HLines = CanvasSizeY/CellSize;			///!///


        g.setColor(new Color(70,70,255));
        for (int i=0; i<HLines+1; i++)
            g.drawLine(0,i*CellSize,CanvasSizeX,i*CellSize);

        for (int j=0; j<VLines+1; j++)
            g.drawLine(j*CellSize,0,j*CellSize,CanvasSizeY);

    }

    void DrawArea(Graphics g) {					//Отрисовка клеток
        int col = FF.getColCount();		///!///
        int row = FF.getRowCount();		///!///

        for (int i=0; i<col; i++)
            for (int j=0; j<row; j++)
                if (FF.getCellValue(i,j)==-1) {
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(i*CellSize, j*CellSize, CellSize, CellSize);
                } else
                if (FF.getCellValue(i,j)==2) {
                    g.setColor(FillColor);
                    g.fillRect(i*CellSize, j*CellSize, CellSize, CellSize);
                } else
                if (FF.getCellValue(i,j) != 0) {
                    g.setColor(Color.darkGray);
                    g.fillRect(i*CellSize, j*CellSize, CellSize, CellSize);
                }


    }

    boolean Mouse2AreaPos(int mx,int my) {				//Калибруем позицию мышки в клетку
        int x = mx/CellSize;
        int y = my/CellSize;

        return FF.setCellValue(x,y,-4);
    }



    boolean Recursive(int x,int y, int oldcolor, int newcolor ) {

        if (done) return true;
        if (( FF.getCellValue(x,y) == oldcolor) || (FF.getCellValue(x,y) == -4 )) {
            try {
                repaint();
                //FF.test += 1;			/////
                Timer.sleep(GetSpeed() / 2);
            }
            catch (InterruptedException e){}


            FF.setCellValue(x,y,newcolor);
            Recursive(x, y-1, oldcolor, newcolor);
            Recursive(x, y+1, oldcolor, newcolor);
            Recursive(x-1, y, oldcolor, newcolor);
            Recursive(x+1, y, oldcolor, newcolor);

        }
        return true;
    }


    public void init() {

        resize(440,450);

        CanvasSizeX = 440;			//Ширина поля отрисовки
        CanvasSizeY = 320;			//Длинна -||-
        CellSize = 10;				//РАЗМЕР КЛЕТКИ
        Alg = 0;

        FF = new FloodFill(CanvasSizeX/CellSize,CanvasSizeY/CellSize);


        FillColor = new Color(67,110,238);
        MenuBgColor = new Color(255,255,255);
        setBackground(MenuBgColor);


        setLayout(null);


        //Интерфейс
        ButtonReset = new Button("СБРОС");
        radioGroup = new CheckboxGroup();

        AnimSpeedGroup = new CheckboxGroup();

        w4 = new Checkbox("4-х", radioGroup,true);
        w8 = new Checkbox("8-ми", radioGroup,false);
        rec = new Checkbox("4-х Рекурсия", radioGroup,false);

        slow = new Checkbox("0.5", AnimSpeedGroup ,false);
        med = new Checkbox("1.0", AnimSpeedGroup ,true);
        fast = new Checkbox("2.0", AnimSpeedGroup ,false);


        int ypoz = 30;
        ButtonReset.setBounds(370,CanvasSizeY+20,50,24);

        slow.setBounds(150,CanvasSizeY+ypoz ,  50,30);
        med.setBounds(150,CanvasSizeY+ypoz+25, 70,30);
        fast.setBounds(150,CanvasSizeY+ypoz+50,50,30);

        w4.setBounds(270,CanvasSizeY+ypoz,    50,30);
        w8.setBounds(270,CanvasSizeY+ypoz+25, 50,30);
        rec.setBounds(270,CanvasSizeY+ypoz+50,105,30);


        add(ButtonReset);

        add(w4);
        add(w8);
        add(rec);

        add(slow);
        add(med);
        add(fast);

        bigFont = new Font("Arial",Font.PLAIN,12);
        setBackground(Color.white);

        dim = getSize();
        offscreen = createImage(dim.width,dim.height);
        bufferGraphics = offscreen.getGraphics();

        ButtonReset.addActionListener(this);
        addMouseListener(this);
    }



    public void stop(){}


    public void paint(Graphics g) {

        bufferGraphics.clearRect(0,0,dim.width,dim.width);

        DrawArea(bufferGraphics);
        DrawGrid(bufferGraphics);

        bufferGraphics.setFont(bigFont);
        bufferGraphics.setColor(MenuBgColor);
        bufferGraphics.fillRect(0,CanvasSizeY,CanvasSizeX,130);
        bufferGraphics.setColor(FillColor);
        bufferGraphics.fillRect(30,CanvasSizeY+105,80,16);




        bufferGraphics.setColor(Color.black);
        bufferGraphics.drawRect(0,CanvasSizeY-1,CanvasSizeX-1,130);

        bufferGraphics.drawString("Скорость заливки:",150,CanvasSizeY+20);
        bufferGraphics.drawString("Метод:",270,CanvasSizeY+20);


        g.drawImage(offscreen,0,0,this);

    }

    public void update(Graphics g) {
        paint(g);
    }

    public void destroy() {
        done = true;
        Timer = null;
    }


    public void actionPerformed(ActionEvent evt) {

        if (evt.getSource() == ButtonReset) {						// сброс
            done = true;
            Timer = null;
            FF.ClearArea();

        }

        repaint();
    }

    public void run(){
        Thread thisThread = Thread.currentThread();
        while ((Timer == thisThread) && (!done)){

            if (Alg == 0 )
                done = FF.Fill();
            else
            if (Alg == 1 )
                done = FF.Fill8();
            else {
                done = Recursive(xpos/CellSize,ypos/CellSize,0,2);

            }

            if (done) FF.ClearArea();


            repaint();

            try {
                thisThread.sleep(GetSpeed());
            }
            catch (InterruptedException e){}

        }
    }

    public void mouseClicked (MouseEvent me) {
        xpos = me.getX();
        ypos = me.getY();

        if ( (done) && (Mouse2AreaPos(xpos,ypos)) ){

            if (w4.getState()) Alg = 0;
            else if (w8.getState()) Alg = 1;
            else Alg = 2;

            done = false;
            Timer = new Thread(this);
            Timer.start();

        }


        repaint();

    }

    public void mousePressed (MouseEvent me) {}

    public void mouseReleased (MouseEvent me) {}

    public void mouseEntered (MouseEvent me) {}

    public void mouseExited (MouseEvent me) {}

}

class FloodFill {
    int[][] Area;
    int cols,rows;
    boolean swap = false;

    public FloodFill(int col,int row) {
        Area = new int[col][row];
        cols = col;
        rows = row;
        ClearArea();
    }

    public void ClearArea() {			// Очистить клетки
        for (int i=0;i<cols;i++)
            for(int j=0;j<rows;j++)

                if ((i==0) || (j==0) || (i==cols-1) || (j==rows-1)) 	//границы
                    Area[i][j] = -1;
                else
                    Area[i][j] = 0;

        //Граница
        for (int i=1;i<13;i++)
            Area[i][10] = -1;

        for (int i=1;i<7;i++)
            Area[i][16] = -1;
//a
        for (int i=38;i<43;i++)
            Area[i][16] = -1;

        for (int i=17;i<24;i++)
            Area[38][i] = -1;

        for (int i=38;i<42;i++)
            Area[i][24] = -1;
//
        for (int i=2;i<8;i++)
            Area[15][i] = -1;

        for (int i=2;i<5;i++)
            Area[5][rows-i] = -1;

        Area[16][8] = -1;
        Area[17][9] = -1;
        Area[18][9] = -1;
        Area[19][8] = -1;

        Area[2][1] = -1;
        Area[2][2] = -1;
        Area[1][2] = -1;

    }

    public int getCellValue(int col,int row){
        if ((col < cols) && (row < rows))
            return Area[col][row];
        else
            return -1;
    }

    public boolean setCellValue(int col,int row,int v){
        if ((col < cols) && (row < rows) && (Area[col][row] != -1 )) {
            Area[col][row] = v;
            return true;
        } else {
            return false;
        }
    }

    public int getColCount(){
        return cols;
    }

    public int getRowCount(){
        return rows;
    }

    public boolean Fill(){			// 4-х
        swap = !swap;

        boolean Finish = true;

        int old,ac;

        if (swap) {
            ac = -2;
            old = -3;

        } else {
            ac  = -3;
            old = -2;

        }
        ////

        for (int i=0;i<cols;i++)
            for(int j=0;j<rows;j++) {
                if ((Area[i][j] == old) || (Area[i][j] == -4)) {		//-4
                    Finish = false;
                    Area[i][j] = 2;
                    if (Area[i+1][j] == 0) Area[i+1][j] = ac;
                    if (Area[i][j+1] == 0) Area[i][j+1] = ac;
                    if (Area[i-1][j] == 0) Area[i-1][j] = ac;
                    if (Area[i][j-1] == 0) Area[i][j-1] = ac;

                }
            }


        return Finish;
    }


    public boolean Fill8(){			//8-ми
        swap = !swap;

        boolean Finish = true;

        int old,ac;

        if (swap) {
            ac = -2;
            old = -3;

        } else {
            ac  = -3;
            old = -2;

        }
        ////

        for (int i=0;i<cols;i++)
            for(int j=0;j<rows;j++) {
                if ((Area[i][j] == old) || (Area[i][j] == -4)) {
                    Finish = false;
                    Area[i][j] = 2;
                    if (Area[i+1][j] == 0) Area[i+1][j] = ac;
                    if (Area[i][j+1] == 0) Area[i][j+1] = ac;
                    if (Area[i-1][j] == 0) Area[i-1][j] = ac;
                    if (Area[i][j-1] == 0) Area[i][j-1] = ac;

                    if (Area[i+1][j+1] == 0) Area[i+1][j+1] = ac;
                    if (Area[i+1][j-1] == 0) Area[i+1][j-1] = ac;
                    if (Area[i-1][j+1] == 0) Area[i-1][j+1] = ac;
                    if (Area[i-1][j-1] == 0) Area[i-1][j-1] = ac;

                }
            }

// остановить перед выходом
        return Finish;
    }
}

 