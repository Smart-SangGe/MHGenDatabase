package com.ghstudios.android.features.skills;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ghstudios.android.AssetLoader;
import com.ghstudios.android.data.classes.ItemToSkillTree;
import com.ghstudios.android.data.cursors.ItemToSkillTreeCursor;
import com.ghstudios.android.features.armor.detail.ArmorSetDetailPagerActivity;
import com.ghstudios.android.loader.ItemToSkillTreeListCursorLoader;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ClickListeners.ArmorClickListener;

public class SkillTreeArmorFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {

	private static final String ARG_SKILL = "SKILLTREE_SKILL";
	private static final String ARG_TYPE = "SKILLTREE_TYPE";
	
	private String skill_type;

	public static SkillTreeArmorFragment newInstance(long skill, String type) {
		Bundle args = new Bundle();
		args.putLong(ARG_SKILL, skill);
		args.putString(ARG_TYPE, type);
		SkillTreeArmorFragment f = new SkillTreeArmorFragment();
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int loaderId = 0;
		String mType = getArguments().getString(ARG_TYPE);
		
		if (mType.equals("Head")) {
			loaderId = R.id.skill_tree_armor_fragment_head;
		}
		else if (mType.equals("Body")) {
			loaderId = R.id.skill_tree_armor_fragment_body;
		}
		else if (mType.equals("Arms")) {
			loaderId = R.id.skill_tree_armor_fragment_arms;
		}
		else if (mType.equals("Waist")) {
			loaderId = R.id.skill_tree_armor_fragment_waist;
		}
		else if (mType.equals("Legs")) {
			loaderId = R.id.skill_tree_armor_fragment_legs;
		}
		
		// Initialize the loader to load the list of runs
		getLoaderManager().initLoader(loaderId, getArguments(), this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_generic_list, null);
		return v;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// The id argument will be the Item ID; CursorAdapter gives us this
		// for free
		
		Intent i = new Intent(getActivity(), ArmorSetDetailPagerActivity.class);
		i.putExtra(ArmorSetDetailPagerActivity.EXTRA_ARMOR_ID, (long) v.getTag());
		startActivity(i);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// You only ever load the runs, so assume this is the case
		long mSkill = -1;
		skill_type = null;
		if (args != null) {
			mSkill = args.getLong(ARG_SKILL);
			skill_type = args.getString(ARG_TYPE);
		}
		return new ItemToSkillTreeListCursorLoader(getActivity(), 
				ItemToSkillTreeListCursorLoader.FROM_SKILL_TREE, mSkill, skill_type);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// Create an adapter to point at this cursor
		ItemToSkillTreeListCursorAdapter adapter = new ItemToSkillTreeListCursorAdapter(
				getActivity(), (ItemToSkillTreeCursor) cursor, skill_type);
		setListAdapter(adapter);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// Stop using the cursor (via the adapter)
		setListAdapter(null);
	}

	private static class ItemToSkillTreeListCursorAdapter extends CursorAdapter {

		private ItemToSkillTreeCursor mItemToSkillTreeCursor;
		private String mType;

		public ItemToSkillTreeListCursorAdapter(Context context, ItemToSkillTreeCursor cursor, String type) {
			super(context, cursor, 0);
			mItemToSkillTreeCursor = cursor;
			mType = type;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// Use a layout inflater to get a row view
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			return inflater.inflate(R.layout.fragment_skill_item_listitem,
					parent, false);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// Get the skill for the current row
			ItemToSkillTree skill = mItemToSkillTreeCursor.getItemToSkillTree();

			// Set up the text view
			LinearLayout root = view.findViewById(R.id.listitem);
			ImageView skillItemImageView = view.findViewById(R.id.item_image);
			TextView skillItemTextView = view.findViewById(R.id.item);
			TextView skillAmtTextView = view.findViewById(R.id.amt);
			
			String nameText = skill.getItem().getName();
			String amtText = "" + skill.getPoints();
			
			skillItemTextView.setText(nameText);
			skillAmtTextView.setText(amtText);

			AssetLoader.setIcon(skillItemImageView,skill.getItem());

			root.setTag(skill.getItem().getId());
            root.setOnClickListener(new ArmorClickListener(context, skill.getItem().getId(),false));
		}
	}

}