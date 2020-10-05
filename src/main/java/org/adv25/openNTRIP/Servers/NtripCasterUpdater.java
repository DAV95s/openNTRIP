package org.adv25.openNTRIP.Servers;

import org.adv25.openNTRIP.Databases.DAO.MountPointDAO;
import org.adv25.openNTRIP.Databases.DAO.NtripCasterDAO;
import org.adv25.openNTRIP.Databases.Models.MountPointModel;
import org.adv25.openNTRIP.Databases.Models.NtripCasterModel;

import java.util.*;

/* Contains methods for update caster */
public abstract class NtripCasterUpdater {

    protected static Timer timer = new Timer();
    protected NtripCasterModel model;
    protected HashMap<String, MountPointModel> mountPoints = new HashMap<>();

    protected TimerTask updateModel = new TimerTask() {
        @Override
        public void run() {
            NtripCasterDAO dao = new NtripCasterDAO();
            NtripCasterModel cas_model = dao.read(model.getId());
            if (cas_model == null)
                close();
            model = cas_model;
        }
    };

    protected abstract void close();


    protected TimerTask updateMountPoints = new TimerTask() {
        @Override
        public void run() {
            MountPointDAO dao = new MountPointDAO();
            mountPoints = dao.getAllByCasterId(model.getId());
        }
    };

}
