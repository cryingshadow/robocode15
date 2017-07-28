
package de.metro.robocode;


import robocode.*;
import robocode.Robot;
import robocode.util.*;

import java.awt.*;


public class GhidoBot extends AdvancedRobot {

    boolean peek; // Don't turn if there's a robot there
    double moveAmount; // How much to move
    boolean movingForward;
    String trackedBy;
    int hitSpam;
    String state = "Wall";

    /**
     * run: Move around the walls
     */

    private void colorRobot()
    {
        // Set colors
        setBodyColor(Color.black);
        setGunColor(Color.black);
        setRadarColor(Color.orange);
        setBulletColor(Color.cyan);
        setScanColor(Color.cyan);

    }

    private void goWall()
    {
        // Look before we turn when ahead() completes.
        peek = true;

        // Move up the wall
        ahead(moveAmount);

        // Don't look now
        peek = false;

        // Turn to the next wall
        turnRight(90);
    }

    private void setUpWall()
    {
        // Initialize peek to false
        peek = false;

        // turnLeft to face a wall.
        // getHeading() % 90 means the remainder of
        // getHeading() divided by 90.
        turnLeft(getHeading() % 90);
        ahead(moveAmount);

        // Turn the gun to turn right 90 degrees.
        peek = true;
        turnGunRight(90);
        turnRight(90);

    }

    public void run() {

        colorRobot();

        // Initialize moveAmount to the maximum possible for this battlefield.
        moveAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());

        if(state.equals("Wall"))
            setUpWall();

        while (true) {
            if(state.equals("Wall"))
                goWall();
            else
                if(state.equals("Crazy"))
                    goCrazy();
        }
    }

    /**
     * onHitRobot:  Move away a bit.
     */
    @Override
    public void onHitRobot(HitRobotEvent e) {
        // If he's in front of us, set back up a bit.
        if (e.getBearing() > -90 && e.getBearing() < 90) {
            back(100);
        } // else he's in back of us, so set ahead a bit.
        else {
            ahead(100);
        }
    }

    /**
     * onScannedRobot:  Fire!
     */
    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        fire(1);

        // Note that scan is called automatically when the robot is moving.
        // By calling it manually here, we make sure we generate another scan event if there's a robot on the next
        // wall, so that we do not start moving up it until it's gone.
        if (peek) {
            scan();
        }
    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        //get name of enemy
        String enemy = event.getName();
        hitSpam++;

        if((enemy == trackedBy) && (hitSpam == 3))
        {
            state = "Crazy";
        }
    }

    private void goCrazy()
    {
        setAhead(40000);
        movingForward = true;
        // Tell the game we will want to turn right 90
        setTurnRight(90);
        // At this point, we have indicated to the game that *when we do something*,
        // we will want to move ahead and turn right.  That's what "set" means.
        // It is important to realize we have not done anything yet!
        // In order to actually move, we'll want to call a method that
        // takes real time, such as waitFor.
        // waitFor actually starts the action -- we start moving and turning.
        // It will not return until we have finished turning.
        waitFor(new TurnCompleteCondition(this));
        // Note:  We are still moving ahead now, but the turn is complete.
        // Now we'll turn the other way...
        setTurnLeft(180);
        // ... and wait for the turn to finish ...
        waitFor(new TurnCompleteCondition(this));
        // ... then the other way ...
        setTurnRight(180);
        // .. and wait for that turn to finish.
        waitFor(new TurnCompleteCondition(this));
        // then back to the top to do it all again
    }



}
