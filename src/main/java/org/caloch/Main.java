package org.caloch;


import org.caloch.beans.Feature;
import org.caloch.core.*;
import org.caloch.utils.Migrator;
import org.caloch.utils.PropertyUtil;


public class Main {
    public static void main(String[] args) throws Exception {
        PropertyUtil propertyUtil = new PropertyUtil();
        new JMvcServer(propertyUtil, args)
                .setAuthenticator(new BasicAdminAuthenticator(propertyUtil))
                .addDbContext(false)
                .start();
//        new QuartzScheduler().run();
//        new Migrator(propertyUtil).run();


    }

}
