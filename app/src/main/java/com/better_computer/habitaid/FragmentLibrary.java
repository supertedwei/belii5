package com.better_computer.habitaid;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.SearchEntry;
import com.better_computer.habitaid.data.core.NonSched;
import com.better_computer.habitaid.data.core.NonSchedHelper;
import com.better_computer.habitaid.form.schedule.NonSchedRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentLibrary extends AbstractBaseFragment {

    protected DatabaseHelper databaseHelper;
    protected NonSchedHelper nonSchedHelper;

    public FragmentLibrary() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_library, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_schedule_library, container, false);
        this.rootView = view;
        return view;
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        this.databaseHelper = DatabaseHelper.getInstance();
        this.nonSchedHelper = DatabaseHelper.getInstance().getHelper(NonSchedHelper.class);

        final ListView listViewCategory = ((ListView) rootView.findViewById(R.id.schedule_category_list));
        final ListView listViewSubcategory = ((ListView) rootView.findViewById(R.id.schedule_subcategory_list));
        final RecyclerView listViewLibrary = ((RecyclerView) rootView.findViewById(R.id.schedule_library_list));
        final NonSchedRecyclerViewAdapter libViewAdapter = new NonSchedRecyclerViewAdapter(context);
        ItemTouchHelper itemTouchHelper = libViewAdapter.getItemTouchHelper();
        itemTouchHelper.attachToRecyclerView(listViewLibrary);
        listViewLibrary.setAdapter(libViewAdapter);

        listViewCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String sCat = listViewCategory.getItemAtPosition(i).toString();

                ((MainActivity) context).sSelectedLibraryCat = sCat;
                ((MainActivity) context).sSelectedLibrarySubcat = "~NONE";

                String sql2 = "SELECT DISTINCT subcat FROM core_tbl_nonsched WHERE cat='" + sCat + "' ORDER BY subcat";

                SQLiteDatabase database2 = databaseHelper.getReadableDatabase();
                Cursor cursor2 = database2.rawQuery(sql2, new String[0]);

                List<String> listSubcat = new ArrayList<String>();
                if (cursor2.moveToFirst()) {
                    do {
                        listSubcat.add(cursor2.getString(0));
                    } while (cursor2.moveToNext());
                }

                //fix - android.database.CursorWindowAllocationException Start
                cursor2.close();
                //fix - android.database.CursorWindowAllocationException End

                ArrayAdapter<String> adapterSubcat = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listSubcat);
                listViewSubcategory.setAdapter(adapterSubcat);

                List<SearchEntry> keys = new ArrayList<SearchEntry>();
                keys.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, sCat));

                List<NonSched> listNonSched = (List<NonSched>) (List<?>) nonSchedHelper.find(keys, "ORDER BY iprio");
                libViewAdapter.setList(listNonSched);

                refresh();
            }
        });

        listViewSubcategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String sSubcat = listViewSubcategory.getItemAtPosition(i).toString();

                ((MainActivity) context).sSelectedLibrarySubcat = sSubcat;

                List<SearchEntry> keys = new ArrayList<SearchEntry>();
                keys.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, ((MainActivity)context).sSelectedLibraryCat));
                keys.add(new SearchEntry(SearchEntry.Type.STRING, "subcat", SearchEntry.Search.EQUAL, sSubcat));

                List<NonSched> listNonSched = (List<NonSched>) (List<?>) nonSchedHelper.find(keys, "ORDER BY iprio");
                libViewAdapter.setList(listNonSched);

                refresh();
            }
        });

        refresh();
   }

    @Override
    public void refresh() {
        String sCat = ((MainActivity) (context)).sSelectedLibraryCat;
        String sSubcat = ((MainActivity) (context)).sSelectedLibrarySubcat;

        final ListView listViewCategory = ((ListView) rootView.findViewById(R.id.schedule_category_list));
        final ListView listViewSubcategory = ((ListView) rootView.findViewById(R.id.schedule_subcategory_list));
        final RecyclerView listViewLibrary = ((RecyclerView) rootView.findViewById(R.id.schedule_library_list));
        final NonSchedRecyclerViewAdapter libViewAdapter = new NonSchedRecyclerViewAdapter(context);
        ItemTouchHelper itemTouchHelper = libViewAdapter.getItemTouchHelper();
        itemTouchHelper.attachToRecyclerView(listViewLibrary);
        listViewLibrary.setAdapter(libViewAdapter);

        SQLiteDatabase database = this.databaseHelper.getReadableDatabase();

        String sql = "SELECT DISTINCT cat FROM core_tbl_nonsched ORDER BY cat";
        Cursor cursor = database.rawQuery(sql, new String[0]);

        List<String> listCat = new ArrayList<String>();
        if (cursor.moveToFirst()) {
            do {
                listCat.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        //fix - android.database.CursorWindowAllocationException Start
        cursor.close();
        //fix - android.database.CursorWindowAllocationException End

        ArrayAdapter<String> adapterCat = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listCat);
        listViewCategory.setAdapter(adapterCat);

        if (sCat.length() > 0) {
            String sql2 = "SELECT DISTINCT subcat FROM core_tbl_nonsched WHERE cat='" + sCat + "' ORDER BY subcat";

            SQLiteDatabase database2 = databaseHelper.getReadableDatabase();
            Cursor cursor2 = database2.rawQuery(sql2, new String[0]);

            List<String> listSubcat = new ArrayList<String>();
            if (cursor2.moveToFirst()) {
                do {
                    listSubcat.add(cursor2.getString(0));
                } while (cursor2.moveToNext());
            }

            //fix - android.database.CursorWindowAllocationException Start
            cursor2.close();
            //fix - android.database.CursorWindowAllocationException End

            ArrayAdapter<String> adapterSubcat = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listSubcat);
            listViewSubcategory.setAdapter(adapterSubcat);

            ///////////////////////////////////////////
            List<SearchEntry> keys = new ArrayList<SearchEntry>();
            keys.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, sCat));

            if (!sSubcat.equalsIgnoreCase("~NONE")) {
                keys.add(new SearchEntry(SearchEntry.Type.STRING, "subcat", SearchEntry.Search.EQUAL, sSubcat));
            }

            List<NonSched> listNonSched = (List<NonSched>) (List<?>) nonSchedHelper.find(keys, "ORDER BY iprio");
            libViewAdapter.setList(listNonSched);
        }
        else {
            List<SearchEntry> keys = new ArrayList<SearchEntry>();
            keys.add(new SearchEntry(SearchEntry.Type.STRING, "cat", SearchEntry.Search.EQUAL, "library"));

            List<NonSched> listNonSched = (List<NonSched>) (List<?>) nonSchedHelper.find(keys, "ORDER BY iprio");
            libViewAdapter.setList(listNonSched);
        }
    }
}