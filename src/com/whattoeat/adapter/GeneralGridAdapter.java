package com.whattoeat.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.whattoeat.R;
import com.whattoeat.utils.LogUtils;

/**
 * 
 * @author froyohuang 所有一行多个item的adapter的基类
 */
public abstract class GeneralGridAdapter extends BaseAdapter
{

    private static String TAG = "GridAdapter";
    protected final Context mContext;
    private final LayoutInflater mInflater;
    //	private View titleView;
    //    protected Handler mHandler;
    protected int mColumnCount = 3;
    private int mColumnWidth = 0;
    protected final int mAppType;

    private static final LinearLayout.LayoutParams llp = new LayoutParams( LayoutParams.MATCH_PARENT , LayoutParams.WRAP_CONTENT );
    private static final LinearLayout.LayoutParams title_llp = new LayoutParams( LayoutParams.MATCH_PARENT , LayoutParams.WRAP_CONTENT );
    /**
     * 空白视图，用于充填没有数据的地方
     */
    private final ArrayList< View > mBlankView;

    public GeneralGridAdapter( Context context , int appType , int columnCount )
    {
	super( );

	mContext = context;
	mColumnCount = columnCount;
	mBlankView = new ArrayList< View >( );
	mInflater = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
	mAppType = appType;
	llp.weight = 1;
    }

    @Override
    public int getCount()
    {
	int nCount = (int)Math.ceil( getAllCount( ) * 1.0 / mColumnCount );
	// Log.d("GridAdapter", "getCount =" + nCount);
	return nCount;
    }

    @Override
    public Object getItem( int position )
    {
	return null;
    }

    @Override
    public long getItemId( int position )
    {
	return 0;
    }

    public void setColumnCount( int count )
    {
	mColumnCount = count;
    }

    @Override
    public View getView( int position , View convertView , ViewGroup parent )
    {
	LinearLayout lineContainer = (LinearLayout)convertView;
	LinearLayout titleContent = null;
	LinearLayout lineContent = null;
	//	LinearLayout topContent = null;

	int nItemWidth = 0;

	// 首先创建带有上下分割线的的整行的Layout

	if( lineContainer == null )
	{

	    LogUtils.e( "lineContainer = null" + "|parent=" + parent + "|paddingleft=" + parent.getPaddingLeft( ) + "|paddingRight=" + parent.getPaddingRight( ) );
	    lineContainer = new LinearLayout( mContext );
	    lineContainer.setOrientation( LinearLayout.VERTICAL );
	    lineContainer.setGravity( Gravity.TOP );
	    lineContainer.setPadding( 0 , 0 , 0 , 0 );

	    lineContent = new LinearLayout( mContext );
	    lineContent.setOrientation( LinearLayout.HORIZONTAL );
	    lineContent.setGravity( Gravity.CENTER_VERTICAL );

	    //			if(position == 0 || position == 2){
	    titleContent = new LinearLayout( mContext );
	    titleContent.setOrientation( LinearLayout.HORIZONTAL );
	    titleContent.setGravity( Gravity.CENTER_VERTICAL );

	    LinearLayout.LayoutParams linelp = new LayoutParams( android.view.ViewGroup.LayoutParams.MATCH_PARENT , android.view.ViewGroup.LayoutParams.MATCH_PARENT );
	    linelp.weight = 1;

	    LinearLayout.LayoutParams titlelp = new LayoutParams( android.view.ViewGroup.LayoutParams.MATCH_PARENT , android.view.ViewGroup.LayoutParams.WRAP_CONTENT );
	    //	    topContent.addView( titleContent , titlelp );

	    lineContainer.addView( titleContent , titlelp );
	    lineContainer.addView( lineContent , linelp );

	    //	    topContent.setTag( R.id.tag_titlecontent , titleContent );
	    lineContainer.setTag( R.id.tag_titlecontent , titleContent );
	    lineContainer.setTag( R.id.tag_linecontent , lineContent );
	}
	else
	{
	    //	    topContent = (LinearLayout)lineContainer.getTag( R.id.tag_topcontent );
	    lineContent = (LinearLayout)lineContainer.getTag( R.id.tag_linecontent );
	    titleContent = (LinearLayout)lineContainer.getTag( R.id.tag_titlecontent );
	    lineContent.removeAllViews( );
	    titleContent.removeAllViews( );
	}

	if( position == 0 )
	{
	    showFillView( position , titleContent , lineContainer );
	}

	// 获取或者初始化重用的itemview和divider的view
	ArrayList< View > viewHolder = (ArrayList< View >)lineContent.getTag( R.id.tag_viewHolder );
	if( viewHolder == null )
	{
	    viewHolder = new ArrayList< View >( );
	    lineContent.setTag( R.id.tag_viewHolder , viewHolder );
	}

	// 对每一行，生成单独的实际itemview加入上面创建的layout中
	int index = position * mColumnCount;
	int end = index + mColumnCount;
	int did = 0;
	int nFillItem = 0;
	for( ; index < end && index < getAllCount( ) ; index++ , did++ )
	{
	    int posInLine = mColumnCount - ( end - index );
	    View itemView = null;

	    if( viewHolder.size( ) > posInLine )
	    {
		itemView = viewHolder.get( posInLine );
		itemView = getPeaceView( index , itemView , lineContent );
	    }
	    else
	    {
		itemView = getPeaceView( index , itemView , lineContent );
		viewHolder.add( itemView );
	    }

	    if( itemView != null )
	    {
		itemView.setTag( R.id.tag_blankView , Boolean.FALSE );
		itemView.setTag( R.id.tag_positionView , Integer.valueOf( index ) );

		llp.setMargins( 6 , 0 , 6 , 0 );

		lineContent.addView( itemView , llp );
		nFillItem++;
	    }
	}

	lineContainer.setTag( R.id.tag_fillItems , nFillItem );

	// 当一行未填满，填充空白view
	if( index < end )
	{
	    for( int blankNum = end - index - 1 ; blankNum >= 0 ; blankNum-- )
	    {
		View bk = null;
		for( int j = mBlankView.size( ) - 1 ; j >= 0 ; j-- )
		{

		    if( mBlankView.get( j ).getParent( ) == null )
		    {
			bk = mBlankView.get( j );
			break;
		    }
		}
		if( bk == null )
		{
		    bk = genBlankView( );
		    mBlankView.add( bk );
		}
		lineContent.addView( bk , llp );
	    }
	}

	return lineContainer;
    }

    private void showFillView( int position , LinearLayout titleContent , LinearLayout lineContainer )
    {
	// TODO Auto-generated method stub
	titleContent.addView( getHorizontalDivider( ) );
    }

    /**
     * 获取列表数据项的总长度
     * 
     * @return
     */
    public abstract int getAllCount();

    /**
     * 获取每个数据项的视图
     * 
     * @param positionInTotal
     * @param convertView
     * @param parentView
     * @return
     */
    public abstract View getPeaceView( int positionInTotal , View convertView , ViewGroup parentView );

    protected View getHorizontalDivider()
    {
	ImageView imageView = new ImageView( mContext );
	//		imageView.setBackgroundResource(R.drawable.divider_horizontal);
	//	imageView.setBackgroundResource( R.color.background );
	LinearLayout.LayoutParams llp = getHorizontalDiveiderLayoutParams( android.view.ViewGroup.LayoutParams.MATCH_PARENT );
	imageView.setLayoutParams( llp );
	return imageView;
    }

    private LinearLayout.LayoutParams getHorizontalDiveiderLayoutParams( int nWidth )
    {
	LinearLayout.LayoutParams llp;
	llp = new LinearLayout.LayoutParams( android.view.ViewGroup.LayoutParams.MATCH_PARENT , 3 );
	return llp;
    }

    //
    //    protected View getVerticalDivider()
    //    {
    //	ImageView imageView = new ImageView( mContext );
    //	LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams( 6 , android.view.ViewGroup.LayoutParams.MATCH_PARENT );
    //	//		imageView.setBackgroundResource(R.drawable.divider_vertical);
    //	imageView.setBackgroundResource( R.color.background );
    //	imageView.setLayoutParams( llp );
    //	return imageView;
    //    }

    protected View genBlankView()
    {
	View blankView = new LinearLayout( mContext );
	blankView.setTag( R.id.tag_blankView , Boolean.TRUE );
	return blankView;
    }
}
