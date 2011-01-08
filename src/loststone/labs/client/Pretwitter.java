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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.List;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Pretwitter implements EntryPoint, EntryUpdateHandler {
  private final VerticalPanel mainPanel = new VerticalPanel();
  private final Label errorLabel = new Label();
  private final PretwitterPanel PretwitterPanel =
      new PretwitterPanel();
  private final Grid PretwitterEntries = new Grid(20, 1);

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    // Set up Pretwitter entries tables, first row for headers.
    PretwitterEntries.setHTML(0, 0, "<b>Message</b>");

    // Style the entries table.
    PretwitterEntries.addStyleName("gb-PretwitterEntries");
    PretwitterEntries.getRowFormatter().addStyleName(0, "gb-PretwitterEntriesHeader");
    PretwitterEntries.getCellFormatter().addStyleName(0, 0, "gb-MessageHeader");

    // Attach components together.
    mainPanel.add(errorLabel);
    mainPanel.add(PretwitterPanel);
    mainPanel.add(PretwitterEntries);

    // Align the signature panel and entries table.
    mainPanel.setWidth("100%");
    mainPanel.setCellHorizontalAlignment(PretwitterPanel,	
        HasAlignment.ALIGN_CENTER);
    mainPanel.setCellHorizontalAlignment(PretwitterEntries,
        HasAlignment.ALIGN_CENTER);
    mainPanel.setCellWidth(PretwitterEntries, "550px");

    // Load and display existing Pretwitter entries.
    loadPretwitterEntries();

    // Attach handlers onto UI components.
    PretwitterPanel.addEntryUpdateHandler(this);

    // Attach main panel to host HTML page.
    RootPanel.get().add(mainPanel);
  }

  /**
   * Loads the ten latest Pretwitter entries and sets them in the Pretwitter entries
   * table.
   */
  private void loadPretwitterEntries() {
    // Get the latest Pretwitter entries.
    PretwitterServiceAsync PretwitterService =
        (PretwitterServiceAsync) GWT.create(PretwitterService.class);
    PretwitterService
        .getTenLatestEntries(
            new AsyncCallback<List<PretwitterEntryTransferObject>>() {
          @Override
          public void onFailure(Throwable caught) {
            updateError("Failed to load Pretwitter entries: "+caught.getMessage());
            errorLabel.setVisible(true);
          }

          @Override
          public void onSuccess(List<PretwitterEntryTransferObject> result) {
            updateEntries(result);
          }
        });
  }

  /**
   * Updates the grid display with the latest Pretwitter entries.
   *
   * @param entries the new entries to update in the display
   */
  public void updateEntries(List<PretwitterEntryTransferObject> entries) {
    // Hide the error label if it's still present.
    if (errorLabel.isVisible()) {
      errorLabel.setVisible(false);
    }

    // Update entries in Pretwitter entries table.
    if (entries != null) {
      // Start at +1 offset to skip table header.
      for (int i = 1; i <= entries.size(); i++) {
        PretwitterEntries.setText(i, 0, entries.get(i - 1).getMessage());
        PretwitterEntries.getCellFormatter().addStyleName(i, 0, "gb-PretwitterEntry");
        if (i % 2 != 0) {
          PretwitterEntries.getRowFormatter().addStyleName(i, "gb-PretwitterEntryOdd");
        }
      }
    }
  }

  @Override
  public void updateError(String errorMessage) {
    // Update error label with error message.
    errorLabel.setText(errorMessage);
    errorLabel.setVisible(true);
  }

  @Override
  public void clearError() {
    errorLabel.setVisible(false);
  }

  /**
   * Gets the error label.
   *
   * @return the error label
   */
  public Label getErrorLabel() {
    return errorLabel;
  }

  /**
   * Gets the Pretwitter entries display grid.
   *
   * @return the Pretwitter entries display grid
   */
  public Grid getPretwitterEntries() {
    return PretwitterEntries;
  }
}

