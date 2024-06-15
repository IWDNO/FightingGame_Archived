package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.characters.Player;
import com.mygdx.game.utils.DamageResult;
import com.mygdx.game.utils.UserData;
import com.mygdx.game.views.MainScreen;

import static com.mygdx.game.utils.Constants.*;


public class GameContactListener implements ContactListener {
    private Player player1, player2;

    public GameContactListener(MainScreen parent) {
        player1 = parent.player1;
        player2 = parent.player2;
    }

    @Override
    public void beginContact(Contact contact) {
        UserData fa = (UserData) contact.getFixtureA().getUserData();
        UserData fb = (UserData) contact.getFixtureB().getUserData();
        if (fa == null || fb == null) return;
        System.out.println("start " + fa.getName() + " + " + fb.getName());

        //normal attack
        if (check(fa, fb, "Player1-NORMAL_ATTACK", "Player2")) {
            ((UserData) findInFixtureList(player1, "Player1-NORMAL_ATTACK").getUserData()).setDead(true);
            DamageResult damage = player1.generateDamage(ATTACK_TYPE.NORMAL_ATTACK);
            player2.takeDamage(damage);


        } else if (check(fa, fb, "Player2-NORMAL_ATTACK", "Player1")) {
            ((UserData) findInFixtureList(player2, "Player2-NORMAL_ATTACK").getUserData()).setDead(true);
            DamageResult damage = player2.generateDamage(ATTACK_TYPE.NORMAL_ATTACK);
            player1.takeDamage(damage);

        }
        // e attack
        if (check(fa, fb, "Player1-E_ATTACK", "Player2")) {
            ((UserData) findInFixtureList(player1, "Player1-E_ATTACK").getUserData()).setDead(true);
            DamageResult damage = player1.generateDamage(ATTACK_TYPE.E_ATTACK);
            Vector2 hitPosition = player2.getBody().getPosition();
            player2.takeDamage(damage);

        } else if (check(fa, fb, "Player2-E_ATTACK", "Player1")) {
            ((UserData) findInFixtureList(player2, "Player2-E_ATTACK").getUserData()).setDead(true);
            DamageResult damage = player2.generateDamage(ATTACK_TYPE.E_ATTACK);
            Vector2 hitPosition = player1.getBody().getPosition();
            player1.takeDamage(damage);
        }
    }

    @Override
    public void endContact(Contact contact) {
        UserData fa = (UserData) contact.getFixtureA().getUserData();
        UserData fb = (UserData) contact.getFixtureB().getUserData();
        if (fa == null || fb == null) return;
        System.out.println("end " + fa.getName() + " + " + fb.getName());
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    private boolean check(UserData data1, UserData data2, String name1, String name2) {
        if ((data1 != null) && (data2 != null)) {
            return ((data1.getName().equals(name1) && data2.getName().equals(name2))
                    || (data2.getName().equals(name1) && data1.getName().equals(name2)));
        }
        return false;
    }

    private Fixture find(Contact contact, String userData) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();
        if ((a.getUserData() != null) && (b.getUserData() != null)) {
            if (a.getUserData().equals(userData)) return a;
            return b;
        }
        return null;
    }

    private Fixture findInFixtureList(Player player, String string) {
        Fixture result = null;
        for (Fixture fixture : player.getBody().getFixtureList()) {
            if ((string).equals(((UserData) fixture.getUserData()).getName())) {
                result = fixture;
                break;
            }
        }
        return result;
    }
}
