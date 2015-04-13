package com.teknologism.exo.addons.customization.listener;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;
import org.exoplatform.social.core.processor.I18NActivityUtils;
import org.exoplatform.social.core.relationship.RelationshipEvent;
import org.exoplatform.social.core.relationship.RelationshipListenerPlugin;
import org.exoplatform.social.core.relationship.model.Relationship;
import org.exoplatform.social.core.storage.api.IdentityStorage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: eric
 * Date: 13/04/15
 * Time: 15:24
 */
public class RelationshipPublisher extends RelationshipListenerPlugin {
    /**
     * The Logger.
     */
    private static final Log LOG = ExoLogger.getExoLogger(RelationshipPublisher.class);

    public static enum TitleId {
        CONNECTION_REQUESTED,
        CONNECTION_CONFIRMED
    }

    public static final String SENDER_PARAM = "SENDER";
    public static final String RECEIVER_PARAM = "RECEIVER";
    public static final String RELATIONSHIP_UUID_PARAM = "RELATIONSHIP_UUID";
    public static final String RELATIONSHIP_ACTIVITY_TYPE = "exosocial:relationship";
    public static final String NUMBER_OF_CONNECTIONS = "NUMBER_OF_CONNECTIONS";
    public static final String USER_ACTIVITIES_FOR_RELATIONSHIP = "USER_ACTIVITIES_FOR_RELATIONSHIP";
    public static final String USER_COMMENTS_ACTIVITY_FOR_RELATIONSHIP = "USER_COMMENTS_ACTIVITY_FOR_RELATIONSHIP";
    public static final String USER_DISPLAY_NAME_PARAM = "USER_DISPLAY_NAME_PARAM";

    private ActivityManager activityManager;

    private IdentityManager identityManager;

    public RelationshipPublisher(InitParams params, ActivityManager activityManager, IdentityManager identityManager) {

        this.activityManager = activityManager;
        this.identityManager = identityManager;
    }


    private ExoSocialActivity createNewActivity(Identity identity, int nbOfConnections) {
        ExoSocialActivity activity = new ExoSocialActivityImpl();
        activity.setType(USER_ACTIVITIES_FOR_RELATIONSHIP);
        updateActivity(activity, nbOfConnections);
        return activity;
    }

    private ExoSocialActivity createNewComment(Identity userIdenity, String fullName) {
        ExoSocialActivityImpl comment = new ExoSocialActivityImpl();
        comment.setType(USER_COMMENTS_ACTIVITY_FOR_RELATIONSHIP);
        String message = String.format("I'm now connected with %s", fullName);
        comment.setTitle(message);
        comment.setUserId(userIdenity.getId());
        I18NActivityUtils.addResourceKey(comment, "user_relation_confirmed", fullName);
        return comment;
    }

    private void updateActivity(ExoSocialActivity activity, int numberOfConnections) {
        String title = String.format("I'm now connected with %s user(s)", numberOfConnections);
        String titleId = "user_relations";
        Map<String, String> params = new HashMap<String, String>();
        activity.setTitle(title);
        activity.setTitleId(null);
        activity.setTemplateParams(params);
        I18NActivityUtils.addResourceKey(activity, titleId, "" + numberOfConnections);
    }

    /**
     * Do not Publish an activity on both user's stream to indicate their new connection but LOG it instead
     */
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


    private void reloadIfNeeded(Identity identity) throws Exception {
        if (identity.getId() == null || identity.getProfile().getFullName().length() == 0) {
            identity = identityManager.getIdentity(identity.getGlobalId().toString(), true);
        }
    }


    /**
     * Gets params (sender, receiver, relationship uuid) from a provided relationship.
     *
     * @param relationship
     * @return
     * @since 1.2.0-GA
     */
    private Map<String, String> getParams(Relationship relationship) throws Exception {
        Identity sender = relationship.getSender();
        reloadIfNeeded(sender);
        Identity receiver = relationship.getReceiver();
        reloadIfNeeded(receiver);
        String senderRemoteId = sender.getRemoteId();
        String receiverRemoteId = receiver.getRemoteId();
        Map<String, String> params = new HashMap<String, String>();
        params.put(SENDER_PARAM, senderRemoteId);
        params.put(RECEIVER_PARAM, receiverRemoteId);
        params.put(RELATIONSHIP_UUID_PARAM, relationship.getId());
        return params;
    }

    private IdentityStorage getIdentityStorage() {
        return (IdentityStorage) PortalContainer.getInstance().getComponentInstanceOfType(IdentityStorage.class);
    }

    private RelationshipManager getRelationShipManager() {
        return (RelationshipManager) PortalContainer.getInstance().getComponentInstanceOfType(RelationshipManager.class);
    }
}
