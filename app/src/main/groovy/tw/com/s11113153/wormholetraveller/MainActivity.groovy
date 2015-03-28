package tw.com.s11113153.wormholetraveller

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.ActionBarActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView

public class MainActivity extends ActionBarActivity {
  private static final String TAG = MainActivity.class.getSimpleName();
  
  Toolbar mToolbar
  TextView mTextView
  MenuItem lineMenuItem, squareMenuItem
  RecyclerView mRecyclerView
  RecycleAdapter mAdapter

  boolean isLine = true

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    init()
    setConfig()
  }

  void init() {
    mToolbar = findViewById(R.id.toolbar) as Toolbar
    mTextView = findViewById(R.id.tvLogo) as TextView
    mRecyclerView = findViewById(R.id.rvFeed) as RecyclerView
  }

  void setConfig() {
    setFontType()
    setUpToolbar()
    setUpRecycleAdapter()
  }

  void setFontType() {
    mTextView.setTypeface(Utils.getFont(this, Utils.FontType.ROBOTO_BOLD_ITALIC))
  }

  void setUpToolbar() {
    setSupportActionBar(mToolbar)
    mToolbar.setNavigationIcon(R.mipmap.ic_menu)
  }

  void setUpRecycleAdapter() {
    mAdapter = new RecycleAdapter(this)
    LinearLayoutManager manager = new LinearLayoutManager(this)
    mRecyclerView.setLayoutManager(manager)
    mRecyclerView.setAdapter(mAdapter)
  }


  @Override
  public void onBackPressed() {
    moveTaskToBack(true);
  }

  @Override
  boolean onPrepareOptionsMenu(Menu menu) {
    if (isLine) {
      lineMenuItem.setVisible(false)
      squareMenuItem.setVisible(true)
    } else {
      lineMenuItem.setVisible(true)
      squareMenuItem.setVisible(false)
    }
    return true
  }

  @Override
  boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_square_article:
        isLine = false
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2))
        break
      case R.id.action_line_article:
        isLine = true
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this))
        break
    }
    mRecyclerView.invalidate()
    invalidateOptionsMenu()
    return true
  }


  @Override
  boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu)
    lineMenuItem = menu.findItem(R.id.action_line_article)
    squareMenuItem = menu.findItem(R.id.action_square_article)
    return true
  }

  static class RecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context
    int itemsCount = 10

    RecycleAdapter(Context context) {
      this.context = context
    }

    @Override
    RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      final View view = LayoutInflater.from(context).inflate(R.layout.item_recycle, parent, false);
      return new ViewHolder(view)
    }

    @Override
    void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      ViewHolder viewHolder = (ViewHolder) holder
      if (position % 2 == 0) {
        Drawable drawable = context.getResources().getDrawable(R.drawable.ic_launcher)
      }
    }

    @Override
    int getItemCount() {
      return itemsCount
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
      final SquaredImageView mSquaredImageView
      final ImageView mImageViewBottom

      ViewHolder(View itemView) {
        super(itemView)
        mSquaredImageView = (SquaredImageView) itemView.findViewById(R.id.ivFeedCenter)
        mImageViewBottom = (ImageView) itemView.findViewById(R.id.ivFeedBottom)
      }
    }
  }
}
