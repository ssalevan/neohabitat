package org.made.neohabitat;

import org.elkoserver.foundation.json.JSONMethod;
import org.elkoserver.foundation.json.OptInteger;
import org.elkoserver.server.context.User;
import org.made.neohabitat.mods.Avatar;

public abstract class Weapon extends HabitatMod {

	/* The activity ID of sitting on the ground */
	public static final int SIT_GROUND = 132;
	/* no effect, beep at player */
	public static final int MISS = 0;
	/* destroy object that is target */
	public static final int DESTROY = 1;
	/* keester avatar that is target */
	public static final int HIT = 2;
	/* kill avatar that is target */
	public static final int DEATH = 3;
	
	public Weapon(OptInteger style, OptInteger x, OptInteger y, OptInteger orientation,
		OptInteger gr_state) {
		super(style, x, y, orientation, gr_state);
	}

    @JSONMethod
    public void HELP(User from) {
    	generic_HELP(from);
    }
    
    @JSONMethod
    public void GET(User from) {
        generic_GET(from);
    }

    @JSONMethod({ "containerNoid", "x", "y", "orientation" })
    public void PUT(User from, OptInteger containerNoid, OptInteger x, OptInteger y, OptInteger orientation) {
        generic_PUT(from, containerNoid.value(THE_REGION), avatar(from).x, avatar(from).y, avatar(from).orientation);
    }
    
    @JSONMethod({ "pointed_noid" })
    public void ATTACK(User from, OptInteger pointed_noid) {
    	generic_ATTACK(from, current_region().noids[pointed_noid.value(0)]);
    }
	
	public void generic_ATTACK(User from, HabitatMod target) {
		if (target == null) {
			send_reply_msg(from, FALSE);
			return;
		}
		int success = TRUE;
		Avatar fromAvatar = avatar(from);
		if (fromAvatar.stun_count > 0) {
			success = FALSE;
			send_private_msg(from, fromAvatar.noid, from, "SPEAK$",
				"I can't attack.  I am stunned.");
		} else if (current_region().nitty_bits[WEAPONS_FREE]) {
			object_broadcast(this.noid, 
				"This is a weapons-free zone.  Your weapon will not operate here.");
		} else if (adjacent(target) || is_ranged_weapon()) {
			HabitatMod damageableTarget = target;
			if (target.HabitatClass() == CLASS_HEAD) {
				// If the weapon is attacking an Avatar's head, set the target to the
				// Avatar which contains it.
				damageableTarget = target.container();
			}
			if (damageableTarget.HabitatClass() == CLASS_AVATAR) {
				Avatar damageableAvatar = (Avatar) damageableTarget;
				damageableAvatar.activity = SIT_GROUND;
				success = damage_avatar(damageableAvatar);
				send_neighbor_msg(from, fromAvatar.noid, "ATTACK$",
					"TARGET_ID", damageableTarget.noid,
					"SUCCESS", success);
			} else {
				success = damage_object(damageableTarget);
				send_neighbor_msg(from, fromAvatar.noid, "BASH$",
					"TARGET_ID", damageableTarget.noid,
					"SUCCESS", success);
			}
		} else {
			success = FALSE;
		}

		send_reply_msg(from, noid,
			"TARGET_ID", target.noid,
			"SUCCESS", success);

		if (success == DEATH) {
			kill_avatar((Avatar) target);
		}
	}

	public int damage_avatar(Avatar who) {
		if (who.health <= 0) {
			return HIT;
		} else if (who.health <= 20) {
			// He's dead!
			who.health -= 20;
			who.gen_flags[MODIFIED] = true;
			who.checkpoint_object(who);
			return DEATH;
		} else {
			// Naw, he's only wounded.
			who.health -= 20;
			who.gen_flags[MODIFIED] = true;
			who.checkpoint_object(who);
			return HIT;
		}
	}
	
	public int damage_object(HabitatMod object) {
		if (damageable(object)) {
			// TODO(steve): Uncomment when object deletion works.
			//destroy_object(object);
			return DESTROY;
		} else {
			return FALSE;
		}
	}
	
	public boolean damageable(HabitatMod object) {
		return object.HabitatClass() == CLASS_MAILBOX;
	}
	
	public boolean is_ranged_weapon() {
		return HabitatClass() == CLASS_GUN;
	}
}
