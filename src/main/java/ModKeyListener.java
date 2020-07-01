import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModKeyListener implements NativeKeyListener {

    public Boolean wPressed = false;
    public Boolean aPressed = false;
    public Boolean sPressed = false;
    public Boolean dPressed = false;
    public Boolean pause = false;

    //Resoulution changes automatically laterw
    public static int screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    public static int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    public static Point screenCentre = new Point(screenWidth/2,screenHeight/2);
    public static Point mousePosPreMovement = new Point(screenWidth/2,screenHeight/2);

    /*Handles Key Presses*/
    Boolean movementButtonPressed;
    public void nativeKeyPressed(NativeKeyEvent e) {

        System.out.println("Pressed " + NativeKeyEvent.getKeyText(e.getKeyCode()));

        if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("P")) { pause = !pause; }
        if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("W")) { wPressed = true; }
        if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("A")) { aPressed = true; }
        if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("S")) { sPressed = true; }
        if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("D")) { dPressed = true; }

        if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("W")
                || NativeKeyEvent.getKeyText(e.getKeyCode()).equals("A")
                || NativeKeyEvent.getKeyText(e.getKeyCode()).equals("S")
                || NativeKeyEvent.getKeyText(e.getKeyCode()).equals("D"))
        {
            movementButtonPressed = true;
            try {
                robot = new Robot();
                //mousePosPreMovement = MouseInfo.getPointerInfo().getLocation();
                doMouseInput();
                robot.mousePress(InputEvent.BUTTON1_MASK);
            } catch (Exception ignored) { }
        }
        else{ movementButtonPressed = false; }
    }

    /*Handles Key Releases*/
    Boolean movementButtonLetGo;
    public void nativeKeyReleased(NativeKeyEvent e) {

        if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("W")
                || NativeKeyEvent.getKeyText(e.getKeyCode()).equals("A")
                || NativeKeyEvent.getKeyText(e.getKeyCode()).equals("S")
                || NativeKeyEvent.getKeyText(e.getKeyCode()).equals("D"))
        {
            movementButtonLetGo = true;
        }
        else{movementButtonLetGo = false;}

        System.out.println("Released " + NativeKeyEvent.getKeyText(e.getKeyCode()));

        if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("W")) { wPressed = false; }
        if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("A")) { aPressed = false; }
        if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("S")) { sPressed = false; }
        if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("D")) { dPressed = false; }

        //If all movement keys are let go
        if(!wPressed && !aPressed && !sPressed && !dPressed && movementButtonLetGo)
        {

            robot.mouseMove(screenCentre.x,screenCentre.y);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
            //robot.mouseMove(mousePosPreMovement.x,mousePosPreMovement.y);
        }
        else{ if(movementButtonLetGo) {
            try {
                doMouseInput();
            } catch (AWTException awtException) {
                awtException.printStackTrace();
            }
        }
        }
    }

    /*IDK What that does but it was in the example*/
    public void nativeKeyTyped(NativeKeyEvent e) {
        System.out.println("Key Typed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
    }

    Robot robot;
    Dimension screenSize;
    public static void main(String[] args) {

        // Get the logger for "org.jnativehook" and set the level to warning.
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.WARNING);

        // Don't forget to disable the parent handlers.
        logger.setUseParentHandlers(false);

        ModKeyListener modKeyListener = new ModKeyListener();
        modKeyListener.init();

        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }
        GlobalScreen.addNativeKeyListener(new ModKeyListener());
    }

    public void init(){
        System.out.println("Initializing ModKeyListener");
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /*Does the final mouse input*/
    Point inputVector;
    public void doMouseInput() throws AWTException {
        if(pause) return;
        inputVector = generateInputVector();
        System.out.println("Moved mouse to X: " + inputVector.x + " Y:" + inputVector.y);
        robot = new Robot();
        robot.mouseMove( (int) inputVector.x, (int) inputVector.y);
    }

    int wInput;
    int aInput;
    int sInput;
    int dInput;

    /*Turns wasd input into analog signal*/
    public Point generateInputVector()
    {
        //Turn boolean into integer
        wInput = wPressed ? (screenHeight/2) * -1 : 0;
        aInput = aPressed ? (screenWidth/2) * -1 : 0;
        sInput = sPressed ? (screenHeight/2) : 0;
        dInput = dPressed ? (screenWidth/2) : 0;

        //Turns key input into screen position
        //W plus A for example is the right upper corner of the screen
        Point output = new Point();

        output.x = (screenWidth / 2) + aInput + dInput;
        output.y = (screenHeight / 2) + wInput + sInput;

        return output;
    }
}