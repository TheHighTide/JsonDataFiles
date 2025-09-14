package com.hightide.jsondatafile;

import com.google.gson.*;
import java.io.*;
import com.hightide.jsondatafile.types.JsonDataList;
import java.nio.file.Path;
import java.util.Set;

/**
 * Represents an easily editable JSON file.
 */
public class JsonDataFile {
    private final File file;
    private final Gson gson;
    private JsonObject root;

    /**
     * The constructor for the JSON file you can edit easily.
     * @param filePath The path the JSON file will be stored at
     * @throws IOException Thrown if the JSON file fails to load
     */
    public JsonDataFile(Path filePath) throws IOException {
        this.file = filePath.toFile();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        load();
    }

    private void load() throws IOException {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            this.root = new JsonObject();
            save();
        } else {
            try (FileReader reader = new FileReader(file)) {
                this.root = gson.fromJson(reader, JsonObject.class);
                if (this.root == null) {
                    this.root = new JsonObject();
                }
            }
        }
    }

    /**
     * Saves the JSON data file and all its data.
     * @throws IOException Thrown if the file fails to save
     */
    public void save() throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(root, writer);
        }
    }

    /**
     * Adds a JSON objet to the JSON data file.
     * @param name The name of the object
     * @param value The value of the object
     * @throws IOException Thrown if the object fails to save to the JSON data file
     */
    public void addObject(String name, Object value) throws IOException {
        root.add(name, gson.toJsonTree(value));
        save();
    }

    /**
     * Gets the object with the specified name and returns it as a JsonElement.
     * @param name The name of the target object
     * @return Returns the target JSON object if it exists
     */
    public JsonElement getObject(String name) {
        return root.get(name);
    }

    /**
     * Removes a JSON list or object from the JSON data file.
     * @param name The name of the target json list/object
     * @return Returns true or false based on if the removal was successful
     * @throws IOException Thrown if something happens during the save of the JSON data file
     */
    public boolean remove(String name) throws IOException {
        if (root.has(name)) {
            root.remove(name);
            save();
            return true;
        }
        return false;
    }

    /**
     * Get a list of all object/list names (keys) in the JSON data file.
     * @return Returns a list of Strings representing all available keys
     */
    public Set<String> listKeys() {
        return root.keySet();
    }

    /**
     * Adds a JSON list to the JSON data file.
     * @param name The name of the list
     * @return Returns the new JSON list
     * @throws IOException Thrown if the JSON data fails to save to the JSON file
     */
    public JsonDataList addList(String name) throws IOException {
        JsonArray array = new JsonArray();
        root.add(name, array);
        save();
        return new JsonDataList(this, name, array);
    }

    /**
     * Gets a list from the JSON data file.
     * @param name The name of the target list
     * @return Returns the specified list
     */
    public JsonDataList getList(String name) {
        if (root.has(name) && root.get(name).isJsonArray()) {
            return new JsonDataList(this, name, root.getAsJsonArray(name));
        }
        return null;
    }

    /**
     * Updates a JSON list in the JSON data file.
     * @param name The name of the target list
     * @param array The updated list
     * @throws IOException Thrown if the json data fails to save to the json data file
     */
    public void updateList(String name, JsonArray array) throws IOException {
        root.add(name, array);
        save();
    }
}