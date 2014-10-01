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
 * The KetaiList class provides an android UI scroll list.  To receive the selection
 * 	data a sketch should define the following method:<br /><br />
 * 
 * void onKetaiListSelection(String selection) - selection is the string selected from the list<br />
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
	 * @param _parent the _parent
	 * @param data the data
	 */
	public KetaiList(PApplet _parent, ArrayList<String> data) {
		super(_parent.getApplicationContext());
		parent = _parent;
		adapter = new ArrayAdapter<String>(parent,
				android.R.layout.simple_list_item_1, data);
		init();

	}

	/**
	 * Instantiates a new ketai list.
	 *
	 * @param _parent the _parent
	 * @param data the data
	 */
	public KetaiList(PApplet _parent, String[] data) {
		super(_parent.getApplicationContext());

		parent = _parent;
		adapter = new ArrayAdapter<String>(parent,
				android.R.layout.simple_list_item_1, data);
		init();
	}

	/**
	 * Instantiates a new ketai list.
	 *
	 * @param _parent the _parent
	 * @param _title the _title
	 * @param data the data
	 */
	public KetaiList(PApplet _parent, String _title, String[] data) {
		super(_parent.getApplicationContext());

		parent = _parent;
		title = _title;
		adapter = new ArrayAdapter<String>(parent,
				android.R.layout.simple_list_item_1, data);
		init();
	}

	/**
	 * Instantiates a new ketai list.
	 *
	 * @param _parent the _parent
	 * @param _title the _title
	 * @param data the data
	 */
	public KetaiList(PApplet _parent, String _title, ArrayList<String> data) {
		super(_parent.getApplicationContext());
		parent = _parent;
		title = _title;
		adapter = new ArrayAdapter<String>(parent,
				android.R.layout.simple_list_item_1, data);
		init();

	}

	/**
	 * Refresh.
	 */
	public void refresh() {
		if (adapter == null)
			return;
		parent.runOnUiThread(new Runnable() {
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
		setBackgroundColor(Color.BLACK);
		setAlpha(1);
		self = this;
		final TextView cancel;

		layout = new RelativeLayout(parent);

		if (title != "") {
			TextView tv = new TextView(parent);
			tv.setText(title);
			setHeaderDividersEnabled(true);
			addHeaderView(tv);
		}

		cancel = new TextView(parent);
		cancel.setText("<CANCEL SELECTION>");

		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				layout.removeAllViewsInLayout();

				self.setVisibility(View.GONE);
				((ViewManager) self.getParent()).removeView(self);
				parent.runOnUiThread(new Runnable() {
					public void run() {
						layout.removeAllViews();
						try {
							parentCallback
									.invoke(parent, new Object[] { self });
						} catch (Exception ex) {
						}
						layout.setVisibility(View.GONE);
					}
				});
			}
		});

		setFooterDividersEnabled(true);
		addFooterView(cancel);
		try {
			parentCallback = parent.getClass().getMethod(
					"onKetaiListSelection", new Class[] { KetaiList.class });
			PApplet.println("Found onKetaiListSelection...");
		} catch (NoSuchMethodException e) {
		}

		setAdapter(adapter);

		setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> p, View view, int position,
					long id) {

				selection = adapter.getItem(position).toString();

				layout.removeAllViewsInLayout();
				try {
					parentCallback.invoke(parent, new Object[] { self });
				} catch (Exception ex) {
				}

				self.setVisibility(View.GONE);
				((ViewManager) self.getParent()).removeView(self);
				parent.runOnUiThread(new Runnable() {
					public void run() {
						layout.removeAllViews();
						layout.setVisibility(View.GONE);
					}
				});
			}
		});

		// add to the main view...

		parent.runOnUiThread(new Runnable() {
			public void run() {
				parent.addContentView(self, new ViewGroup.LayoutParams(
						ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.FILL_PARENT));
			}
		});

	}
}
