package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;


//import com.mygdx.game.bodies.Player;
import com.mygdx.game.characters.Player;
import com.mygdx.game.utils.DamageResult;
import com.mygdx.game.views.MainScreen;

public class GameContactListener implements ContactListener {
    private MainScreen parent;

    public GameContactListener(MainScreen parent) {
        this.parent = parent;
    }


    @Override
    public void beginContact(Contact contact) {

//        Fixture fa = contact.getFixtureA();
//        Fixture fb = contact.getFixtureB();
//        System.out.println(fa.getBody().getUserData() + " + " + fb.getBody().getUserData());

        if (check(contact, "Player1-attack", "Player2")) {
            Player player1 = parent.player1, player2 = parent.player2;
            DamageResult damage = player1.generateDamage(1);

            Vector2 hitPosition = contact.getFixtureA().getBody().getUserData().equals("Player1-attack") ?
                    contact.getFixtureB().getBody().getPosition() : contact.getFixtureA().getBody().getPosition();
            player2.takeDamage(damage, hitPosition);

        } else if (check(contact, "Player2-attack", "Player1")) {
            Player player1 = parent.player1, player2 = parent.player2;
            DamageResult damage = player2.generateDamage(1);

            Vector2 hitPosition = contact.getFixtureA().getBody().getUserData().equals("Player2-attack") ?
                    contact.getFixtureB().getBody().getPosition() : contact.getFixtureA().getBody().getPosition();
            player1.takeDamage(damage, hitPosition);
        }

        if (check(contact, "Player1-eAttack", "Player2")) {
            Player player1 = parent.player1, player2 = parent.player2;
            DamageResult damage = player1.generateDamage(2);

            Vector2 hitPosition = contact.getFixtureA().getBody().getUserData().equals("Player1-eAttack") ?
                    contact.getFixtureB().getBody().getPosition() : contact.getFixtureA().getBody().getPosition();
            player2.takeDamage(damage, hitPosition);

        } else if (check(contact, "Player2-eAttack", "Player1")) {
            Player player1 = parent.player1, player2 = parent.player2;
            DamageResult damage = player2.generateDamage(2);

            Vector2 hitPosition = contact.getFixtureA().getBody().getUserData().equals("Player2-eAttack") ?
                    contact.getFixtureB().getBody().getPosition() : contact.getFixtureA().getBody().getPosition();
            player1.takeDamage(damage, hitPosition);
        }
    }

    @Override
    public void endContact(Contact contact) {

    }
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    private boolean check(Contact contact, String data1, String data2) {
        Body a = contact.getFixtureA().getBody();
        Body b = contact.getFixtureB().getBody();
        if ((a.getUserData() != null) && (b.getUserData() != null)) {
            return (a.getUserData().equals(data1) && b.getUserData().equals(data2))
                    || (a.getUserData().equals(data2) && b.getUserData().equals(data1));
        }
        return false;
    }
}
