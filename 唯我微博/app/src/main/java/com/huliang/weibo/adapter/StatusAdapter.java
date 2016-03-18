package com.huliang.weibo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huliang.weibo.R;
import com.huliang.weibo.activity.ShowImageActivity;
import com.huliang.weibo.activity.StatusDetailActivity;
import com.huliang.weibo.activity.UserInfoActivity;
import com.huliang.weibo.activity.WriteCommentActivity;
import com.huliang.weibo.activity.WriteStatusActivity;
import com.huliang.weibo.entity.PicUrls;
import com.huliang.weibo.entity.Status;
import com.huliang.weibo.entity.User;
import com.huliang.weibo.utils.DateUtils;
import com.huliang.weibo.utils.StringUtils;
import com.huliang.weibo.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class StatusAdapter extends BaseAdapter implements OnClickListener, AdapterView.OnItemClickListener {

    private Context context;
    private List<Status> datas;
    private ImageLoader imageLoader;

    public StatusAdapter(Context context, List<Status> datas) {
        this.context = context;
        this.datas = datas;
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Status getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_status, null);
            holder.ll_card_content = (LinearLayout) convertView.findViewById(R.id.ll_card_content);
            holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            holder.rl_content = (RelativeLayout) convertView.findViewById(R.id.rl_content);
            holder.tv_subhead = (TextView) convertView.findViewById(R.id.tv_subhead);
            holder.tv_caption = (TextView) convertView.findViewById(R.id.tv_caption);

            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.include_status_image = (FrameLayout) convertView.findViewById(R.id.include_status_image);
            //原始微博的图片显示
            holder.gv_images = (GridView) holder.include_status_image.findViewById(R.id.gv_images);
            holder.iv_image = (ImageView) holder.include_status_image.findViewById(R.id.iv_image);
            holder.iv_image.setOnClickListener(this);
            holder.gv_images.setOnItemClickListener(this);

            holder.include_retweeted_status = (LinearLayout) convertView.findViewById(R.id.include_retweeted_status);
            holder.tv_retweeted_content = (TextView) holder.include_retweeted_status
                    .findViewById(R.id.tv_retweeted_content);
            holder.include_retweeted_status_image = (FrameLayout) holder.include_retweeted_status
                    .findViewById(R.id.include_status_image);
            //转发微博的图片显示
            holder.gv_retweeted_images = (GridView) holder.include_retweeted_status_image.findViewById(R.id.gv_images);
            holder.iv_retweeted_image = (ImageView) holder.include_retweeted_status_image.findViewById(R.id.iv_image);
            holder.iv_retweeted_image.setOnClickListener(this);
            holder.gv_retweeted_images.setOnItemClickListener(this);

            holder.ll_share_bottom = (LinearLayout) convertView.findViewById(R.id.ll_share_bottom);
            holder.iv_share_bottom = (ImageView) convertView.findViewById(R.id.iv_share_bottom);
            holder.tv_share_bottom = (TextView) convertView.findViewById(R.id.tv_share_bottom);
            holder.ll_comment_bottom = (LinearLayout) convertView.findViewById(R.id.ll_comment_bottom);
            holder.iv_comment_bottom = (ImageView) convertView.findViewById(R.id.iv_comment_bottom);
            holder.tv_comment_bottom = (TextView) convertView.findViewById(R.id.tv_comment_bottom);
            holder.ll_like_bottom = (LinearLayout) convertView.findViewById(R.id.ll_like_bottom);
            holder.iv_like_bottom = (ImageView) convertView.findViewById(R.id.iv_like_bottom);
            holder.tv_like_bottom = (TextView) convertView.findViewById(R.id.tv_like_bottom);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // bind data
        final Status status = getItem(position);
        final User user = status.getUser();
        imageLoader.displayImage(user.getProfile_image_url(), holder.iv_avatar);
        holder.iv_avatar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserInfoActivity.class);
                intent.putExtra("uid", user.getId());
                context.startActivity(intent);
            }
        });
        holder.tv_subhead.setText(user.getName());
        holder.tv_caption.setText(DateUtils.getShortTime(status.getCreated_at()) + " 来自 " + Html.fromHtml(status.getSource()));
        holder.tv_content.setText(StringUtils.getWeiboContent(context, holder.tv_content, status.getText()));

        setImages(status, holder.include_status_image, holder.gv_images, holder.iv_image);

        final Status retweeted_status = status.getRetweeted_status();
        if (retweeted_status != null) {
            User retUser = retweeted_status.getUser();
            holder.include_retweeted_status.setVisibility(View.VISIBLE);
            String retweetedContent;
            try {
                retweetedContent = "@" + retUser.getName() + ":" + retweeted_status.getText();
            } catch (Exception e) {
                retweetedContent = "微博已被原作者删除";
            }
            holder.tv_retweeted_content
                    .setText(StringUtils.getWeiboContent(context, holder.tv_retweeted_content, retweetedContent));
            setImages(retweeted_status, holder.include_retweeted_status_image, holder.gv_retweeted_images,
                    holder.iv_retweeted_image);
        } else {
            holder.include_retweeted_status.setVisibility(View.GONE);
        }

        holder.tv_share_bottom.setText(status.getReposts_count() == 0 ? "转发" : status.getReposts_count() + "");

        holder.tv_comment_bottom.setText(status.getComments_count() == 0 ? "评论" : status.getComments_count() + "");

        holder.tv_like_bottom.setText(status.getAttitudes_count() == 0 ? "赞" : status.getAttitudes_count() + "");

        holder.ll_card_content.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StatusDetailActivity.class);
                intent.putExtra("status", status);
                context.startActivity(intent);
            }
        });

        holder.include_retweeted_status.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StatusDetailActivity.class);
                intent.putExtra("status", retweeted_status);
                context.startActivity(intent);
            }
        });

        holder.ll_share_bottom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WriteStatusActivity.class);
                intent.putExtra("status", status);
                context.startActivity(intent);
            }
        });

        holder.ll_comment_bottom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.getComments_count() > 0) {
                    Intent intent = new Intent(context, StatusDetailActivity.class);
                    intent.putExtra("status", status);
                    intent.putExtra("scroll2Comment", true);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, WriteCommentActivity.class);
                    intent.putExtra("status", status);
                    context.startActivity(intent);
                }
            }
        });

        holder.ll_like_bottom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(context, "点赞，由于受系统API限制，故未开放", Toast.LENGTH_SHORT);
            }
        });

        return convertView;
    }

    private void setImages(Status status, FrameLayout imgContainer, GridView gv_images, ImageView iv_image) {
        ArrayList<PicUrls> pic_urls = status.getPic_urls();
        String bmiddle_pic = status.getBmiddle_pic();
        gv_images.setOnItemClickListener(this);
        if (pic_urls != null && pic_urls.size() > 1) {
            imgContainer.setVisibility(View.VISIBLE);
            gv_images.setVisibility(View.VISIBLE);
            iv_image.setVisibility(View.GONE);
            StatusGridImgsAdapter gvAdapter = new StatusGridImgsAdapter(context, pic_urls);
            gv_images.setAdapter(gvAdapter);
        } else if (bmiddle_pic != null) {
            imgContainer.setVisibility(View.VISIBLE);
            gv_images.setVisibility(View.GONE);
            iv_image.setVisibility(View.VISIBLE);
            iv_image.setOnClickListener(this);
            iv_image.setTag(status.getOriginal_pic());
            imageLoader.displayImage(bmiddle_pic, iv_image);
        } else {
            imgContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, ShowImageActivity.class);
        String url = (String) v.getTag();
        intent.putExtra("ImageURL", url);
        context.startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(context, ShowImageActivity.class);
        StatusGridImgsAdapter.ViewHolder viewHolder = (StatusGridImgsAdapter.ViewHolder) view.getTag();
        String url = (String) viewHolder.iv_image.getTag();
        intent.putExtra("ImageURL", url);
        context.startActivity(intent);
    }

    public static class ViewHolder {
        public LinearLayout ll_card_content;
        public ImageView iv_avatar;
        public RelativeLayout rl_content;
        public TextView tv_subhead;
        public TextView tv_caption;

        public TextView tv_content;
        public FrameLayout include_status_image;
        public GridView gv_images;
        public ImageView iv_image;

        public LinearLayout include_retweeted_status;
        public TextView tv_retweeted_content;
        public FrameLayout include_retweeted_status_image;
        public GridView gv_retweeted_images;
        public ImageView iv_retweeted_image;

        public LinearLayout ll_share_bottom;
        public ImageView iv_share_bottom;
        public TextView tv_share_bottom;
        public LinearLayout ll_comment_bottom;
        public ImageView iv_comment_bottom;
        public TextView tv_comment_bottom;
        public LinearLayout ll_like_bottom;
        public ImageView iv_like_bottom;
        public TextView tv_like_bottom;
    }
}