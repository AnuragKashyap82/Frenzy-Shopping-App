package kashyap.anurag.frenzystore.Adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import kashyap.anurag.frenzystore.Models.AddressModel;
import kashyap.anurag.frenzystore.R;

import static kashyap.anurag.frenzystore.Activities.DeliveryActivity.SELECT_ADDRESS;
import static kashyap.anurag.frenzystore.Activities.MyAddressActivity.refreshItem;
import static kashyap.anurag.frenzystore.Fragments.MyAccountFragment.MANAGE_ADDRESS;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.HolderAddress> {

    private Context context;
    private ArrayList<AddressModel> addressModelArrayList;
    private int MODE;
    private int preSelectedPosition;

    public AddressAdapter(Context context, ArrayList<AddressModel> addressModelArrayList, int MODE) {
        this.context = context;
        this.addressModelArrayList = addressModelArrayList;
        this.MODE = MODE;
    }

    @NonNull
    @Override
    public HolderAddress onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.addresses_item_layout, parent, false);
        return new HolderAddress(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderAddress holder, int position) {
        AddressModel addressModel = addressModelArrayList.get(position);
        String name = addressModel.getName();
        String area = addressModel.getArea();
        String pinCode = addressModel.getPinCode();
        String state = addressModel.getState();
        String city = addressModel.getCity();
        String landmark = addressModel.getLandmark();
        String phoneNo = addressModel.getPhoneNo();
        ImageView checkedBtn = addressModel.getCheckedBtn();
        ImageView moreBtn = addressModel.getMoreBtn();
        Boolean selected = addressModel.getSelected();

        holder.name.setText(name);
        holder.address.setText(city + " "+area +"\n"+ landmark + "\n"+ state );
        holder.pincode.setText("Pincode: "+pinCode);

        if (MODE == SELECT_ADDRESS){

            if (selected){
                holder.checkedBtn.setVisibility(View.VISIBLE);
                holder.moreBtn.setVisibility(View.GONE);
                preSelectedPosition = position;
            }else {
                holder.checkedBtn.setVisibility(View.GONE);
                holder.moreBtn.setVisibility(View.GONE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (preSelectedPosition != position){
                        addressModelArrayList.get(position).setSelected(true);
                        addressModelArrayList.get(preSelectedPosition).setSelected(false);
                        refreshItem(preSelectedPosition, position);
                        preSelectedPosition = position;
                    }

                }
            });
        }else if (MODE == MANAGE_ADDRESS){

            holder.checkedBtn.setVisibility(View.GONE);
            holder.moreBtn.setVisibility(View.VISIBLE);
        }

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });
        holder.checkedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "checked btn clicked!!!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showAlertDialog() {
        Toast.makeText(context, "more btn selected!!!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return addressModelArrayList.size();
    }

    public class HolderAddress extends RecyclerView.ViewHolder {

        private TextView name, address, pincode;
        private ImageView checkedBtn, moreBtn;

        public HolderAddress(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            pincode = itemView.findViewById(R.id.pincode);
            checkedBtn = itemView.findViewById(R.id.checkedBtn);
            moreBtn = itemView.findViewById(R.id.moreBtn);
        }
    }
}
