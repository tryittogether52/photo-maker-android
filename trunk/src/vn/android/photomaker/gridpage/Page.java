package vn.android.photomaker.gridpage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vn.android.photomaker.entities.Picture;

public class Page {

	private List<Picture> items = new ArrayList<Picture>();

	public List<Picture> getItems() {
		return items;
	}

	public void setItems(List<Picture> items) {
		this.items = items;
	}

	public void addItem(Picture item) {
		items.add(item);
	}

	public void swapItems(int itemA, int itemB) {
		Collections.swap(items, itemA, itemB);
	}

	public Picture removeItem(int itemIndex) {
		Picture item = items.get(itemIndex);
		items.remove(itemIndex);
		return item;
	}

	public void deleteItem(int itemIndex) {
		items.remove(itemIndex);
	}

	public Picture getItem(int itemIndex) {
		Picture item = items.get(itemIndex);
		return item;
	}
}
