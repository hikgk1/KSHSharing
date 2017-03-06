package hbv601g.kshsharing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kristófer Guðni Kolbeins
 */
class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private ArrayList<UserImageContainer> galleryList;
    private Context context;
    private final String mImgUrl;

    RecyclerAdapter(Context applicationContext, ArrayList<UserImageContainer> imgList) {
        this.galleryList = imgList;
        this.context = applicationContext;
        mImgUrl = (String) context.getResources().getText(R.string.url_downloadImg);
    }

    // Sækir layoutið sem er notað fyrir myndirnar
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_layout, parent, false);
        return new ViewHolder(view);
    }

    // Setja mynd/nafn í holderinn þegar RecycleView biður um það
    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
        holder.title.setText(galleryList.get(position).getName());
        holder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Bitmap img = null;
        try {
            img = new GetImageTask().execute(position).get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.img.setImageBitmap(img);

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Kemur seinna",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private ImageView img;

        ViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.cell_title);
            img = (ImageView) view.findViewById(R.id.cell_img);
        }
    }

    private class GetImageTask extends AsyncTask<Integer, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Integer... message) {
            Bitmap res = null;
            InputStream in = null;
            try {
                String urlString =  mImgUrl + galleryList.get(message[0]).getUuid() + galleryList.get(message[0]).getEnding();
                in = new URL(urlString).openStream();
                res = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try { if(in != null) in.close(); } catch (Exception e) { e.printStackTrace(); }
            }
            return res;
        }
    }
}
