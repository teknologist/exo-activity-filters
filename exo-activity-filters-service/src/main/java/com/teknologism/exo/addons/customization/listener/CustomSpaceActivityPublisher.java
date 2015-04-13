/*
 * Copyright (C) 2003-2014 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU Affero General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.teknologism.exo.addons.customization.listener;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.application.SpaceActivityPublisher;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.spi.SpaceLifeCycleEvent;

/**
 * Created by Eric Taïeb Walch for Teknologism SAS
 * www.teknologism.com
 * April 13, 2015
 */
/**
 * This listener overrides the default SpaceActivityPublisher
 * to don't show activities when users join/leave a space
 *
 */


public class CustomSpaceActivityPublisher extends SpaceActivityPublisher {
  /**
   * The Logger.
   */
  private static final Log LOG = ExoLogger.getExoLogger(CustomSpaceActivityPublisher.class);

  public CustomSpaceActivityPublisher(InitParams params,
      ActivityManager activityManager, IdentityManager identityManager) {
    super(params, activityManager, identityManager);
    LOG.info("CustomSpaceActivityPublisher initialized");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void joined(SpaceLifeCycleEvent event) {
    LOG.info("CustomSpaceActivityPublisher: user " + event.getTarget() + " joined space " + event.getSpace().getDisplayName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void left(SpaceLifeCycleEvent event) {
    LOG.info("CustomSpaceActivityPublisher: user " + event.getTarget() + " has left of space " + event.getSpace().getDisplayName());
  }
}
