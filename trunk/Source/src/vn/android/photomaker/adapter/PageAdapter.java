package vn.android.photomaker.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vn.android.photomaker.R;
import vn.android.photomaker.common.ConstantVariable;
import vn.android.photomaker.database.PictureDB;
import vn.android.photomaker.entities.Picture;
import vn.android.photomaker.gridpage.Page;
import vn.android.photomaker.gridpage.PagedDragDropGridAdapter;
import vn.android.photomaker.utils.ScreenUtil;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 
 * Class:PageAdapter.java
 * 
 */
public class PageAdapter implements PagedDragDropGridAdapter {

	/** context of activity. */
	private Activity context;

	/** list of Page. */
	List<Page> pages = new ArrayList<Page>();

	/** give total of page. */
	private int total = 0;

	/** ScreenUtil for load many layout. */
	private ScreenUtil sUtil;

	private PictureDB db;

	public PageAdapter(Activity context, ScreenUtil sUtil) {
		this.db = new PictureDB();
		this.context = context;
		this.sUtil = sUtil;
		reload();
	}

	/**
	 * Used to reload data.
	 */
	public void reload() {
		// Get number of page.
		List<Picture> list = db.select();
		if (list != null && list.size() > 0) {
			Picture picture = list.get(list.size() - 1);
			total = picture.getPageIndex() + 1;
		}

		Page mPage;
		if (total > 0) {
			for (int i = 0; i < total; ++i) {
				mPage = new Page();
				List<Picture> items = db.selectAll(i);
				mPage.setItems(items);
				pages.add(mPage);
			}
		} else {
			List<Picture> items = new ArrayList<Picture>();
			mPage = new Page();
			mPage.setItems(items);
			pages.add(mPage);
		}
	}

	@Override
	public int pageCount() {
		return pages.size();
	}

	private List<Picture> itemsInPage(int page) {
		if (pages.size() > page) {
			return pages.get(page).getItems();
		}
		return Collections.emptyList();
	}

	@Override
	public View view(int page, int index) {

		// Get child view.
		LayoutInflater inflate = LayoutInflater.from(context);
		View view = inflate.inflate(sUtil.getResourceID(R.array.grid_item),
				null);

		return view;
	}

	/**
	 * Used to get picture from index.
	 * 
	 * @param page
	 * @param index
	 *            index of picture.
	 * @return Picture.
	 */
	private Picture getItem(int page, int index) {
		List<Picture> items = itemsInPage(page);
		return items.get(index);
	}

	@Override
	public int rowCount() {
		return AUTOMATIC;
	}

	@Override
	public int columnCount() {
		return AUTOMATIC;
	}

	@Override
	public int itemCountInPage(int page) {
		return itemsInPage(page).size();
	}

	private Page getPage(int pageIndex) {
		return pages.get(pageIndex);
	}

	@Override
	public void swapItems(int pageIndex, int itemIndexA, int itemIndexB) {
		Picture a = pages.get(pageIndex).getItems().get(itemIndexA);
		Picture b = pages.get(pageIndex).getItems().get(itemIndexB);
		if (a != null && b != null) {
			int tg = a.getIndex();
			a.setIndex(b.getIndex());
			b.setIndex(tg);
		}
		getPage(pageIndex).swapItems(itemIndexA, itemIndexB);
		/*
		 * albumDB.updateIndex(a); albumDB.updateIndex(b);
		 */
	}

	@Override
	public void moveItemToPreviousPage(int pageIndex, int itemIndex) {
		int leftPageIndex = pageIndex - 1;
		int newPos = -1;
		if (leftPageIndex >= 0) {
			Page startpage = getPage(pageIndex);
			Page landingPage = getPage(leftPageIndex);
			Picture item = startpage.getItem(itemIndex);
			List<Picture> list = landingPage.getItems();
			item.setPageIndex(leftPageIndex);
			if (list != null && list.size() == ConstantVariable.PAGE_SIZE) {
				Picture lastItem = list.get(list.size() - 1);
				newPos = lastItem.getIndex();
				moveItemToNextPage(leftPageIndex, list.size() - 1);
			}
			if (newPos == -1) {
				if (list != null && list.size() > 0) {
					Picture lastItem = list.get(list.size() - 1);
					newPos = lastItem.getIndex() + 1;
				}
			}
			startpage.removeItem(itemIndex);
			item.setIndex(newPos);
			landingPage.addItem(item);
			// albumDB.updateIndex(item);
		}
	}

	@Override
	public void moveItemToNextPage(int pageIndex, int itemIndex) {
		int rightPageIndex = pageIndex + 1;
		int newPos = -1;
		if (rightPageIndex < pageCount()) {
			Page startpage = getPage(pageIndex);
			Page landingPage = getPage(rightPageIndex);
			Picture item = startpage.getItem(itemIndex);
			item.setPageIndex(rightPageIndex);
			List<Picture> list = landingPage.getItems();
			if (list != null && list.size() == ConstantVariable.PAGE_SIZE) {
				Picture lastItem = list.get(list.size() - 1);
				newPos = lastItem.getIndex();
				moveItemToPreviousPage(rightPageIndex, list.size() - 1);
			}
			if (newPos == -1) {
				if (list != null && list.size() > 0) {
					Picture lastItem = list.get(list.size() - 1);
					newPos = lastItem.getIndex() + 1;
				}
			}
			startpage.removeItem(itemIndex);
			item.setIndex(newPos);
			landingPage.addItem(item);
			// albumDB.updateIndex(item);
		}
	}

	@Override
	public void deleteItem(int pageIndex, int itemIndex) {
		getPage(pageIndex).deleteItem(itemIndex);
	}

	@Override
	public int getPictureID(int position) {
		int i = 0;
		for (Page item : pages) {
			List<Picture> pictures = item.getItems();
			if (pictures != null && pictures.size() > 0) {
				for (int j = 0; j < pictures.size(); ++j) {
					if (i == position) {
						return pictures.get(j).getId();
					}
					++i;
				}
			}
		}
		return -1;
	}
}
