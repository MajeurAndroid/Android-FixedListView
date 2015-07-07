# Android Fixed List View

This library provide a fixed list view, e.g a list view that doesn't handle scroll itself. This means that this list view load all of its adapter's item when setting the adapter, there is no view recycling because the list view cannot know which items are shown and which items aren't. That's why **this library is not relevant for big lists**.

![alt tag](https://raw.githubusercontent.com/MajeurAndroid/Android-FixedListView/master/web_res/device-2015-07-08-000043.png)

### Use case
  - **Your list do not have a lot of items**
  - Your layout is already in a ScrollView or something similar, and you can't add a regular ListView because of scroll conflicts
  -  You just need to show a small number of rows, but adding items to a LinearLayout manually is too dirty coding for you (I agree)
  - You need to add a list in a complex layout, and don't need a complex regular ListView

### Use
Just as a regular framework ListView :

    <com.majeur.fixedlistview.FixedListView 
            xmlns:z="http://schemas.android.com/apk/res-auto"
            android:id="@+id/fixedListView"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            z:dividerColor="@color/divider"
            z:dividerHeight="0.5dp"
            z:dividersType="middle|end" />
            
---------------------

        // Dividers
        fixedListView.setDividerColor(Color.GREEN);
        fixedListView.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.yourDimen));
        fixedListView.setDividersType(FixedListView.DIVIDERS_MIDDLE|FixedListView.DIVIDERS_END);
        
        // Adapter
        fixedListView.setAdapter(new FixedListViewAdapter() {
            @Override
            public int getCount() {
                return yourList.size();
            }
            
            @Override
            public View getView(@NonNull FixedListView parent, int position) {
                View view = getLayoutInflater().inflate(R.layout.list_item, parent, false);
                TextView label = (TextView) view.findViewById(R.id.text);
                label.setText(yourList.get(position).getLabel());
                return view;
            }
        });
        
        // Item click listener
        fixedListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull FixedListView parent, @NonNull View v, int position) {
                Toast.makeText(MainActivity.this, "Item " + position + " clicked", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Item long click listener
        fixedListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull FixedListView parent, @NonNull View view, int position) {
                Toast.makeText(MainActivity.this, "Item " + position + " long clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

### Xml Attributes
 - dividerColor - type : color
 - dividerHeight - type : dimen
 - dividerType - type : flag - must be one of : none beginning end middle

### License:
 - Application code - [Apache 2.0]

[Apache 2.0]:http://www.apache.org/licenses/LICENSE-2.0

