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
package loststone.labs.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Pretwitter signature widget class, containing the components needed to sign the
 * Pretwitter.
 */
public class PretwitterPanel extends Composite implements ClickHandler,
    FiresEntryUpdates {
  private final HorizontalPanel PretwitterPanel;
  private final TextBox PretwitterMessageBox;
  private final Label PretwitterMessageLabel;
  private final Button signButton;
  private boolean PretwitterError = false;

  /* Register for entry update handlers listening for Pretwitter signatures */
  private List<EntryUpdateHandler> entryUpdateHandlers;

  /**
   * Creates a Pretwitter signature panel, with components fully instantiated and
   * attached. 
   * 
   */
  public PretwitterPanel() {
    entryUpdateHandlers = new ArrayList<EntryUpdateHandler>();
    PretwitterPanel = new HorizontalPanel();

    // Create Pretwitter signature panel widgets
    PretwitterMessageBox = new TextBox();
    PretwitterMessageLabel = new Label("Message:");
    signButton = new Button("Pretwittea!");

    // Set up the widget Pretwitter signature panel widget styles (additional to
    // standard styles)
    PretwitterMessageLabel.addStyleName("gb-Label");
    PretwitterMessageBox.addStyleName("gb-MessageBox");

    // Attach components together
    PretwitterPanel.add(PretwitterMessageLabel);
    PretwitterPanel.add(PretwitterMessageBox);
    PretwitterPanel.add(signButton);

    signButton.addClickHandler(this);

   /* The initWidget(Widget) method inherited from the Composite class must be
    * called exactly once in the constructor to declare the widget that this 
    * composite class is wrapping. */
    initWidget(PretwitterPanel);
  }

  /**
   * Listens for the Pretwitter entry update event, and persists the Pretwitter entry
   */
  @Override
  public void onClick(ClickEvent event) {
    if ("".equals(PretwitterMessageBox.getText())) {
      PretwitterError = true;
      fireUpdateError("Please make sure you've filled in all fields");
    } else {
      // Make the entry
      PretwitterEntryTransferObject entry =
          new PretwitterEntryTransferObject(PretwitterMessageBox.getText());
      PretwitterServiceAsync Pretwitter =
          (PretwitterServiceAsync) GWT.create(PretwitterService.class);
      Pretwitter.addPretwitterEntry(entry,
          new AsyncCallback<List<PretwitterEntryTransferObject>>() {
            @Override
            public void onFailure(Throwable caught) {
              fireUpdateError("Failed to add new entry to Pretwitter book"+caught.getMessage());
            }

            @Override
            public void onSuccess(List<PretwitterEntryTransferObject> result) {
              // Fire an update
              fireUpdate(result);
            }
          });
    }
  }

  /**
   * Fires an update error and passes the error message to all registered update
   * handlers
   * 
   * @param errorMessage the error message to pass on
   */
  protected void fireUpdateError(String errorMessage) {
    for (EntryUpdateHandler handler : entryUpdateHandlers) {
      handler.updateError(errorMessage);
    }
  }

  /**
   * Fires an update and passes the entries to all registered update handlers
   * 
   * @param entries the latest Pretwitter entries to be displayed
   */
  protected void fireUpdate(List<PretwitterEntryTransferObject> entries) {
    for (EntryUpdateHandler handler : entryUpdateHandlers) {
      // Signal that any previous sign in errors are now cleared to all
      // registered handlers
      if (PretwitterError) {
        handler.clearError();
        PretwitterError = false;
      }

      handler.updateEntries(entries);
    }
  }

  @Override
  public void addEntryUpdateHandler(EntryUpdateHandler handler) {
    entryUpdateHandlers.add(handler);
  }

  @Override
  public void removeEntryUpdateHandler(EntryUpdateHandler handler) {
    entryUpdateHandlers.remove(handler);
  }
}
