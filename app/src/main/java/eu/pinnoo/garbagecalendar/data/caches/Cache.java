/* 
 * Copyright 2014 Wouter Pinnoo
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.pinnoo.garbagecalendar.data.caches;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.pinnoo.garbagecalendar.data.LocalConstants;

/**
 *
 * @author Wouter Pinnoo <pinnoo.wouter@gmail.com>
 */
public class Cache<T extends Serializable> {

    private File dir;

    public Cache(File dir) {
        if (!dir.exists()) {
            boolean mkdirs = dir.mkdirs();
            if (!mkdirs) {
                Log.e(LocalConstants.LOG, "Error making initial directories for cache");
            }
        }
        this.dir = dir;
    }

    public T get(String key) {
        T value = null;
        try {
            if (exists(key)) {
                ObjectInputStream stream = new ObjectInputStream(new FileInputStream(new File(dir, key)));
                value = (T) stream.readObject();
                stream.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassCastException ex) {
            Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return value;
        }
    }

    public void put(String key, T value) {
        try {
            ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(new File(dir, key)));
            stream.writeObject(value);
            stream.close();
        } catch (IOException ex) {
            Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isSet(String key) {
        File cached = new File(dir, key);
        return cached.length() > 0;
    }

    public long lastModified(String key) {
        File cached = new File(dir, key);
        if (cached.exists()) {
            return cached.lastModified();
        }
        return -1;
    }

    public List<T> getAll() {
        List<T> cached = new ArrayList<T>();
        for (File f : dir.listFiles()) {
            T item = get(f.getName());
            if (item != null) {
                cached.add(item);
            }
        }
        return cached;
    }

    public void invalidate(String key) {
        File cached = new File(dir, key);
        if (cached.exists()) {
            boolean deleted = cached.delete();
            if (!deleted) {
                Log.e(LocalConstants.LOG, "Error deleting file");
            }
        }
    }

    public boolean exists(String key) {
        return new File(dir, key).exists();
    }

    public void clear() {
        for (File f : dir.listFiles()) {
            boolean deleted = f.delete();
            if (!deleted) {
                Log.e(LocalConstants.LOG, "Error deleting file");
            }
        }
    }
}
