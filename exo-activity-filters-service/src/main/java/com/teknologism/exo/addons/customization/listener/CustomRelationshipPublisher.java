package com.teknologism.exo.addons.customization.listener;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.application.RelationshipPublisher;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.relationship.RelationshipEvent;
import org.exoplatform.social.core.relationship.model.Relationship;

/**
 * Created by IntelliJ IDEA.
 * User: eric
 * Date: 13/04/15
 * Time: 15:24
 */
public class CustomRelationshipPublisher extends RelationshipPublisher {
    /**
     * The Logger.
     */
    private static final Log LOG = ExoLogger.getExoLogger(CustomRelationshipPublisher.class);


    private IdentityManager identityManager;


    public CustomRelationshipPublisher(InitParams params, ActivityManager activityManager, IdentityManager identityManager) {
        super(params, activityManager, identityManager);
        this.identityManager = identityManager;
    }

    @Override
    public void confirmed(RelationshipEvent event) {
        Relationship relationship = event.getPayload();
          try {
              Identity sender = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, relationship.getSender().getRemoteId(), true);
              Identity receiver = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, relationship.getReceiver().getRemoteId(), true);
              LOG.info("CustomRelationshipPublisher: user " + sender.getId() + " connected with " + receiver.getId());
          } catch (Exception e) {
              e.printStackTrace();
          }

    }

    @Override
    public void ignored(RelationshipEvent event) {
        LOG.info("CustomRelationshipPublisher: ignored");
    }

    @Override
    public void removed(RelationshipEvent event) {
        LOG.info("CustomRelationshipPublisher: removed");
    }

    @Override
    public void requested(RelationshipEvent event) {
        LOG.info("CustomRelationshipPublisher: requested");
    }

    @Override
    public void denied(RelationshipEvent event) {
        LOG.info("CustomRelationshipPublisher: denied");
    }
}
