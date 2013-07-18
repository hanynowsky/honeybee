package org.otika.honeybee.util;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.otika.honeybee.model.Witness;

/**
 * Pesistence Listener. Must be declared in concerned entities. Can serve as a
 * persistence logger
 *
 * @author Hanine H.A.M <hanynowsky@gmail.com>
 *
 */
public class EntityDebugListener {

    @PrePersist
    void prePersist(Object object) {
        System.out.println("prePersist");
    }

    @PostPersist
    void postPersist(Object object) {
        System.out.println("postPersist");
    }

    @PreUpdate
    void preUpdate(Object object) {
        System.out.println("preUpdate");
    }

    @PostUpdate
    void postUpdate(Object object) {
        System.out.println("postUpdate");
    }

    @PreRemove
    void preRemove(Object object) {
        System.out.println("preRemove");
    }

    @PostRemove
    void postRemove(Object object) {
        System.out.println("postRemove");
    }

    @PostLoad
    void postLoad(Object object) {
        System.out.println(getClass().getName() + " : postLoad");
        System.out.println(object.getClass());
        System.out.println("postLoaded at: " + new Date());

        try {
            if (object instanceof Witness) {
                System.out.println("Witness Id: " + ((Witness) object).getId());
            }
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.INFO, "Exception in postLoad{0}", ((Witness) object).getId());
            Logger.getLogger(getClass().getName()).info(ex.getMessage());
        }
    }
} // END OF CLASS
