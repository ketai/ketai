/*
 * 
 */
package ketai.ui;

import java.lang.reflect.Method;
import java.util.ArrayList;

import processing.core.PApplet;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * The KetaiList class provides an android UI scroll list. To receive the
 * selection data a sketch should define the following method:<br />
 * <br />
 * 
 * void onKetaiListSelection(String selection) - selection is the string
 * selected from the list<br />
 */
public class KetaiList extends ListView {

	/** The parent. */
	private PApplet parent;

	/** The adapter. */
	private ArrayAdapter<String> adapter;

	/** The name. */
	String name = "KetaiList";

	/** The selection. */
	String selection = "";

	/** The self. */
	ListView self;

	/** The layout. */
	RelativeLayout layout;

	/** The parent callback. */
	private Method parentCallback;

	/** The title. */
	String title = "";

	/**
	 * Instantiates a new ketai list.
	 *
	 * @param _parent
	 *            the _parent
	 * @param data
	 *            the data
	 */
	public KetaiList(PApplet _parent, ArrayList<String> data) {
		super(_parent.getActivity().getApplicationContext());
		parent = _parent;
		adapter = new ArrayAdapter<String>(parent.getActivity(), android.R.layout.simple_list_item_1, data);
		init();

	}

	/**
	 * Instantiates a new ketai list.
	 *
	 * @param _parent
	 *            the _parent
	 * @param data
	 *            the data
	 */
	public KetaiList(PApplet _parent, String[] data) {
		super(_parent.getActivity().getApplicationContext());

		parent = _parent;
		adapter = new ArrayAdapter<String>(parent.getActivity(), android.R.layout.simple_list_item_1, data);
		init();
	}

	/**
	 * Instantiates a new ketai list.
	 *
	 * @param _parent
	 *            the _parent
	 * @param _title
	 *            the _title
	 * @param data
	 *            the data
	 */
	public KetaiList(PApplet _parent, String _title, String[] data) {
		super(_parent.getActivity().getApplicationContext());

		parent = _parent;
		title = _title;
		adapter = new ArrayAdapter<String>(parent.getActivity(), android.R.layout.simple_list_item_1, data);
		init();
	}

	/**
	 * Instantiates a new ketai list.
	 *
	 * @param _parent
	 *            the _parent
	 * @param _title
	 *            the _title
	 * @param data
	 *            the data
	 */
	public KetaiList(PApplet _parent, String _title, ArrayList<String> data) {
		super(_parent.getActivity().getApplicationContext());
		parent = _parent;
		title = _title;
		adapter = new ArrayAdapter<String>(parent.getActivity(), android.R.layout.simple_list_item_1, data);
		init();

	}

	/**
	 * Refresh.
	 */
	public void refresh() {
		if (adapter == null)
			return;
		parent.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				adapter.notifyDataSetChanged();
			}
		});
	}

	/**
	 * Gets the selection.
	 *
	 * @return the selection
	 */
	public String getSelection() {
		return selection;
	}

	/**
	 * Inits the.
	 */
	private void init() {
		setBackgroundColor(Color.LTGRAY);
		setAlpha(1);
		self = this;

		layout = new RelativeLayout(parent.getActivity());

		if (title != "") {
			TextView tv = new TextView(parent.getActivity());
			tv.setText(title);
			setHeaderDividersEnabled(true);
			addHeaderView(tv);
		}

		try {
			parentCallback = parent.getClass().getMethod("onKetaiListSelection", new Class[] { KetaiList.class });
			PApplet.println("Found onKetaiListSelection...");
		} catch (NoSuchMethodException e) {
		}

		setAdapter(adapter);

		setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> p, View view, int position, long id) {

				selection = adapter.getItem(position).toString();

				layout.removeAllViewsInLayout();
				try {
					parentCallback.invoke(parent, new Object[] { self });
				} catch (Exception ex) {
				}

				self.setVisibility(View.GONE);
				((ViewManager) self.getParent()).removeView(self);
				parent.getActivity().runOnUiThread(new Runnable() {
					public void run() {
						layout.removeAllViews();
						layout.setVisibility(View.GONE);
					}
				});
			}
		});

		// add to the main view...

		parent.getActivity().runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				parent.getActivity().addContentView(self, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.FILL_PARENT));
			}
		});

	}
}
