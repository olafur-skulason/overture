package project.Entrytypes;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class R3 implements Record {
    public project.Entrytypes.R4 r4;

    //@ public instance invariant project.Entry.invChecksOn ==> inv_R3(r4);
    public R3(final project.Entrytypes.R4 _r4) {
        r4 = (_r4 != null) ? Utils.copy(_r4) : null;
    }

    /*@ pure @*/
    public boolean equals(final Object obj) {
        if (!(obj instanceof project.Entrytypes.R3)) {
            return false;
        }

        project.Entrytypes.R3 other = ((project.Entrytypes.R3) obj);

        return Utils.equals(r4, other.r4);
    }

    /*@ pure @*/
    public int hashCode() {
        return Utils.hashCode(r4);
    }

    /*@ pure @*/
    public project.Entrytypes.R3 copy() {
        return new project.Entrytypes.R3(r4);
    }

    /*@ pure @*/
    public String toString() {
        return "mk_Entry`R3" + Utils.formatFields(r4);
    }

    /*@ pure @*/
    public project.Entrytypes.R4 get_r4() {
        project.Entrytypes.R4 ret_5 = r4;

        //@ assert ret_5 != null;
        return ret_5;
    }

    public void set_r4(final project.Entrytypes.R4 _r4) {
        //@ assert _r4 != null;
        r4 = _r4;

        //@ assert r4 != null;
    }

    /*@ pure @*/
    public Boolean valid() {
        return true;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_R3(final project.Entrytypes.R4 _r4) {
        return !(Utils.equals(_r4.x, 3L));
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_T3(final Object check_t3) {
        if ((Utils.equals(check_t3, null)) ||
                !(Utils.is_(check_t3, project.Entrytypes.R3.class) ||
                Utils.is_(check_t3, project.Entrytypes.X.class))) {
            return false;
        }

        Object t3 = ((Object) check_t3);

        Boolean andResult_1 = false;

        Boolean orResult_1 = false;

        if (!(Utils.is_(t3, project.Entrytypes.R3.class))) {
            orResult_1 = true;
        } else {
            project.Entrytypes.R4 apply_9 = null;

            if (t3 instanceof project.Entrytypes.R3) {
                apply_9 = ((project.Entrytypes.R3) t3).get_r4();
            } else {
                throw new RuntimeException("Missing member: r4");
            }

            orResult_1 = !(Utils.equals(apply_9.get_x(), 10L));
        }

        if (orResult_1) {
            Boolean orResult_2 = false;

            if (!(Utils.is_(t3, project.Entrytypes.X.class))) {
                orResult_2 = true;
            } else {
                Boolean apply_10 = null;

                if (t3 instanceof project.Entrytypes.X) {
                    apply_10 = ((project.Entrytypes.X) t3).get_b();
                } else {
                    throw new RuntimeException("Missing member: b");
                }

                orResult_2 = apply_10;
            }

            if (orResult_2) {
                andResult_1 = true;
            }
        }

        return andResult_1;
    }
}