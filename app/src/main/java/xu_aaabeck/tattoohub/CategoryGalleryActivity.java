package xu_aaabeck.tattoohub;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import adapters.SimpleGridViewAdapter;
import model.Category;

public class CategoryGalleryActivity extends AppCompatActivity {

    private ArrayList<String> categoryPhotos = new ArrayList<>();
    private GridView Grid;
    private SimpleGridViewAdapter adapter;
    private String user;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_gallery);

        Intent i = getIntent();
        categoryName = i.getStringExtra("selectedCategory");
        user = i.getStringExtra("user");

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference categoriesRef = database.getReference("categories");

        categoriesRef.child(user).child(categoryName)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {

                    Category category = categorySnapshot.getValue(Category.class);

                    if(category.getUrl() != null)
                        categoryPhotos.add(category.getUrl());
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                adapter.notifyDataSetChanged();

            }
        });

        Grid = findViewById(R.id.MyGrid);
        adapter = new SimpleGridViewAdapter(getApplicationContext(), 0, categoryPhotos);
        adapter.notifyDataSetChanged();
        Grid.setAdapter(adapter);

        Grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                final Object[] images = categoryPhotos.toArray();

                Intent go = new Intent(getApplicationContext(), FullImageActivity.class);
                go.putExtra("photo", (String) images[position]);
              //By position Clicked
               startActivity(go);
           }
        });

    }


    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onStop(){
        super.onStop();
        //finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        adapter.notifyDataSetChanged();
        finish();
    }

}
