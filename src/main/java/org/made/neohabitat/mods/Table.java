package org.made.neohabitat.mods;

import org.elkoserver.foundation.json.JSONMethod;
import org.elkoserver.foundation.json.OptBoolean;
import org.elkoserver.foundation.json.OptInteger;
import org.elkoserver.json.EncodeControl;
import org.elkoserver.json.JSONLiteral;
import org.made.neohabitat.Copyable;
import org.made.neohabitat.HabitatMod;
import org.made.neohabitat.Openable;

/**
 * Habitat Table Mod (attached to an Elko Item.)
 * 
 * A Table is a large container that can be open/closed and [un]locked.
 * 
 * @author randy
 *
 */

public class Table extends Openable implements Copyable {
    
    public int HabitatClass() {
        return CLASS_TABLE;
    }
    
    public String HabitatModName() {
        return "Table";
    }
    
    public int capacity() {
        return 5;
    }
    
    public int pc_state_bytes() {
        return 3;
    };
    
    public boolean known() {
        return true;
    }
    
    public boolean opaque_container() {
        return false;
    }
    
    public boolean  changeable       () { return true; }

    public boolean filler() {
        return false;
    }
    
    @JSONMethod({ "style", "x", "y", "orientation", "gr_state", "restricted", "open_flags", "key_lo", "key_hi" })
    public Table(OptInteger style, OptInteger x, OptInteger y, OptInteger orientation, OptInteger gr_state, OptBoolean restricted,
        OptInteger open_flags, OptInteger key_lo, OptInteger key_hi) {
        super(style, x, y, orientation, gr_state, restricted, open_flags, key_lo, key_hi, new OptInteger(0));
    }

    public Table(int style, int x, int y, int orientation, int gr_state, boolean restricted, boolean[] open_flags, int key_lo, int key_hi) {
        super(style, x, y, orientation, gr_state, restricted, open_flags, key_lo, key_hi);
    }

    @Override
    public HabitatMod copyThisMod() {
        return new Table(style, x, y, orientation, gr_state, restricted, open_flags, key_lo, key_hi);
    }

    @Override
    public JSONLiteral encode(EncodeControl control) {
        JSONLiteral result = super.encodeOpenable(new JSONLiteral(HabitatModName(), control));
        result.finish();
        return result;
    }
    
}
