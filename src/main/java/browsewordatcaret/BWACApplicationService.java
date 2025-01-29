/*
 * Copyright 2021 Minas Manthos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package browsewordatcaret;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.RoamingType;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

@Service
@State(name = "BWACSettings", storages = {@Storage(value = "$APP_CONFIG$/options.xml", roamingType = RoamingType.DISABLED)})
public final class BWACApplicationService implements PersistentStateComponent<BWACApplicationService.BWACSettings> {
    private BWACSettings settings = new BWACSettings();

    @NotNull
    public static BWACApplicationService getService() {
        return ApplicationManager.getApplication().getService(BWACApplicationService.class);
    }

    @NotNull
    @Override
    public BWACSettings getState() {
        return settings;
    }

    @Override
    public void loadState(@NotNull BWACSettings state) {
        this.settings = state;
    }

    public static class BWACSettings {
        public boolean autoHighlight;
        public boolean wrapAround;
        public boolean humpBound;
    }
}
