/*
 * Copyright 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package loststone.labs.server;

import loststone.labs.client.PretwitterService;
import loststone.labs.client.PretwitterEntryTransferObject;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

/**
 * The guest service implementation class responsible for managing Pretwitter
 * entries.
 */
public class PretwitterServiceImpl extends RemoteServiceServlet implements
    PretwitterService {

  // A clock used in persisting guest entry timestamps.
  private final Clock clock;

  /**
  * Creates an instance of the guest service implementation class, required
  * for the GWT.create() call used to instantiate the RPC service interface.
  */
  public PretwitterServiceImpl(){
    this(new RealTimeClock());
  }

  /**
  * Constructor taking a clock used for persisting Pretwitter entry timestamps.
  */ 
  public PretwitterServiceImpl(Clock clock) {
    super();
    this.clock = clock;
  }

  @Override
  public List<PretwitterEntryTransferObject> addPretwitterEntry(
      PretwitterEntryTransferObject entry) {
    PersistenceManager pm = PersistenceManagerHelper.getPersistenceManager();
    try {
      // Create a new Pretwitter entry and persist
      pm.currentTransaction().begin();
      PretwitterEntry entryToPersist = new PretwitterEntry(entry);
      entryToPersist.setTimestamp(clock.getTime());
      pm.makePersistent(entryToPersist);
      pm.currentTransaction().commit();
      return getTenLatestEntries();
    } finally {
      if (pm.currentTransaction().isActive()) {
        pm.currentTransaction().rollback();
      }
    }
  }

  @Override
  public List<PretwitterEntryTransferObject> getTenLatestEntries() {
    PersistenceManager pm = PersistenceManagerHelper.getPersistenceManager();
    try {
      // Set the query to get the ten latest guest entries
      Query query = pm.newQuery(PretwitterEntry.class);
      query.setOrdering("timestamp DESC");
      query.setRange("0, 10");
      List<PretwitterEntry> entries = (List<PretwitterEntry>) query.execute();

      // Create a new Pretwitter entry transfer object for each entry and add
      // them to the list
      List<PretwitterEntryTransferObject> entryTransferObjects =
          new ArrayList<PretwitterEntryTransferObject>(entries.size());
      for (PretwitterEntry entry : entries) {
        entryTransferObjects.add(new PretwitterEntryTransferObject(entry.getMessage()));
      }
      return entryTransferObjects;
    } finally {
      if (pm.currentTransaction().isActive()) {
        pm.currentTransaction().rollback();
      }
    }
  }
}

