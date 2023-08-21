package com.example.civiladvocacy.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.civiladvocacy.OfficialActivity;
import com.example.civiladvocacy.R;
import com.example.civiladvocacy.models.Office;
import com.example.civiladvocacy.models.Official;

import org.parceler.Parcels;

import java.util.List;

public class OfficialListAdapter extends RecyclerView.Adapter<OfficialListAdapter.GovernmentListViewHolder>{

private final Context mContext;
private final List<Official> mOfficials;
private final List<Office> mOffices;
private final String mSearchTerm;

public OfficialListAdapter(Context context, List<Official> officials, List<Office> offices, String searchTerm) {
        mContext = context;
        mOffices = offices;
        mOfficials = officials;
        mSearchTerm = searchTerm;
        }

@NonNull
@Override
public GovernmentListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GovernmentListViewHolder(LayoutInflater.from(mContext).inflate(R.layout.official_list_item, parent, false));
        }

@Override
public void onBindViewHolder(@NonNull GovernmentListViewHolder holder, int position) {
        holder.bind(mOffices.get(position));
        }

@Override
public int getItemCount() {
        return mOffices.size();
        }

public class GovernmentListViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{

    private final Context mContext;
    TextView mOfficialTag, mOfficialName;
    ImageView mOfficialImage;
    List<Integer> indices;

    public GovernmentListViewHolder(@NonNull View itemView) {
        super(itemView);
        mContext = itemView.getContext();

        mOfficialImage = itemView.findViewById(R.id.iv_official);
        mOfficialName = itemView.findViewById(R.id.tv_official_name);
        mOfficialTag = itemView.findViewById(R.id.tv_official_tag);

        itemView.setOnClickListener(this);
    }

    public void bind(Office office){
        mOfficialTag.setText(office.getName());

        indices = office.getOfficialIndices();

        mOfficialName.setText(mOfficials.get(indices.get(0)).getName());

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(100, 100);
        Glide.with(mContext).load(mOfficials.get(indices.get(0)).getPhotoUrl())
                .apply(requestOptions)
                .placeholder(AppCompatResources.getDrawable(mContext, R.drawable.missing))
                .into(mOfficialImage);

    }

    @Override
    public void onClick(View v) {
        int itemPosition = getLayoutPosition();

        Intent intent = new Intent(mContext, OfficialActivity.class);

        intent.putExtra("position", itemPosition);
        intent.putExtra("official", Parcels.wrap(mOfficials.get(indices.get(0))));
        intent.putExtra("office", Parcels.wrap(mOffices.get(itemPosition)));
        intent.putExtra("searchTerm", mSearchTerm);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
}
}
