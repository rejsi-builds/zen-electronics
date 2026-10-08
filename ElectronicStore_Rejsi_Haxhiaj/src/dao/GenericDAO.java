package dao;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GenericDAO<T extends Serializable> {

	private final File DATA_FILE;
	private final ObservableList<T> data = FXCollections.observableArrayList();

	public GenericDAO(String filePath) {
		this.DATA_FILE = new File(filePath);
		if (DATA_FILE.exists() && DATA_FILE.length() > 0) {
			loadFromFile();
		}
	}

	public ObservableList<T> getAll() {
		return data;
	}

	private void loadFromFile() {
		try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
			while (true) {
				T obj = (T) inputStream.readObject();
				data.add(obj);
			}
		} catch (EOFException ignored) {

		} catch (IOException | ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	public boolean create(T obj) {
		try (FileOutputStream fos = new FileOutputStream(DATA_FILE, true)) {
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			data.add(obj);
			
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public boolean delete(T obj) {
		if (!data.contains(obj))
			return false;

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
			for (T d : data) {
				if (!d.equals(obj)) {
					oos.writeObject(d);
				}
			}
			data.remove(obj);
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public boolean deleteAll(List<T> objsToRemove) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
			for (T d : data) {
				if (!objsToRemove.contains(d)) {
					oos.writeObject(d);
				}
			}
			data.removeAll(objsToRemove);
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public boolean updateAll() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
			for (T d : data) {
				oos.writeObject(d);
			}
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}
}
