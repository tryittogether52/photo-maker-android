package vn.android.photomaker.gridpage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

public class PagedDragDropGrid extends HorizontalScrollView implements
		PagedContainer {

	private int mActivePage = 0;
	private DragDropGrid grid;
	private PagedDragDropGridAdapter adapter;
	private OnItemGridClickListener listener;
	private OnRearrangeListener rearrange;
	private OnPageChangeListener pageChange;

	public PagedDragDropGrid(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initPagedScroll();
		initGrid();
	}

	public PagedDragDropGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPagedScroll();
		initGrid();
	}

	public PagedDragDropGrid(Context context) {
		super(context);
		initPagedScroll();
		initGrid();
	}

	public PagedDragDropGrid(Context context, AttributeSet attrs, int defStyle,
			PagedDragDropGridAdapter adapter) {
		super(context, attrs, defStyle);
		this.adapter = adapter;
		initPagedScroll();
		initGrid();
	}

	public PagedDragDropGrid(Context context, AttributeSet attrs,
			PagedDragDropGridAdapter adapter) {
		super(context, attrs);
		this.adapter = adapter;
		initPagedScroll();
		initGrid();
	}

	public PagedDragDropGrid(Context context, PagedDragDropGridAdapter adapter) {
		super(context);
		this.adapter = adapter;
		initPagedScroll();
		initGrid();
	}

	private void initGrid() {
		grid = new DragDropGrid(getContext());
		addView(grid);
	}

	public void initPagedScroll() {

		setScrollBarStyle(SCROLLBARS_INSIDE_OVERLAY);
		setHorizontalScrollBarEnabled(false);

		setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP
						|| event.getAction() == MotionEvent.ACTION_CANCEL) {
					int scrollX = getScrollX();
					int onePageWidth = v.getMeasuredWidth();
					int page = ((scrollX + (onePageWidth / 2)) / onePageWidth);
					scrollToPage(page);
					return true;
				} else {
					return false;
				}
			}
		});
	}

	public void setAdapter(PagedDragDropGridAdapter adapter) {
		this.adapter = adapter;
		grid.setAdapter(adapter);
		grid.setContainer(this);
	}

	public void setOnRearrangeListener(OnRearrangeListener l) {
		this.rearrange = l;
		grid.setOnRearrangeListener(l);
	}

	public void setOnItemGridClickListener(OnItemGridClickListener l) {
		this.listener = l;
		grid.setOnItemGridListener(l);
	}

	public void notifyDataSetChanged() {
		removeAllViews();
		initGrid();
		grid.setAdapter(adapter);
		grid.setContainer(this);
		grid.setOnItemGridListener(listener);
		grid.setOnRearrangeListener(rearrange);
	}

	@Override
	public void scrollToPage(int page) {
		mActivePage = page;
		int onePageWidth = getMeasuredWidth();
		int scrollTo = page * onePageWidth;
		smoothScrollTo(scrollTo, 0);
		if (pageChange != null)
			pageChange.onPageChange(page);
	}

	@Override
	public void scrollLeft() {
		int newPage = mActivePage - 1;
		if (canScrollToPreviousPage()) {
			scrollToPage(newPage);
		}
	}

	@Override
	public void scrollRight() {
		int newPage = mActivePage + 1;
		if (canScrollToNextPage()) {
			scrollToPage(newPage);
		}
	}

	@Override
	public int currentPage() {
		return mActivePage;
	}

	@Override
	public void enableScroll() {
		requestDisallowInterceptTouchEvent(false);
	}

	@Override
	public void disableScroll() {
		requestDisallowInterceptTouchEvent(true);
	}

	@Override
	public boolean canScrollToNextPage() {
		int newPage = mActivePage + 1;
		return (newPage < adapter.pageCount());
	}

	@Override
	public boolean canScrollToPreviousPage() {
		int newPage = mActivePage - 1;
		return (newPage >= 0);
	}

	// OTHER METHODS
	public void setOnPageChangeListener(OnPageChangeListener l) {
		this.pageChange = l;
	}
}
