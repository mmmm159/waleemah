package com.example.sofra.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sofra.R;
import com.example.sofra.data.api.ApiService;
import com.example.sofra.data.api.RetrofitClient;
import com.example.sofra.data.local.SharedPreference;
import com.example.sofra.data.model.general.itemrestaurant.ItemRestaurantData;
import com.example.sofra.data.model.restaurant.editcategory.EditCategory;
import com.example.sofra.utils.Utils;
import com.example.sofra.view.activity.HomeActivity;
import com.example.sofra.view.fragment.homecycle.restaurant.HomeRestaurantCategoryFragment;
import com.example.sofra.view.fragment.homecycle.restaurant.HomeRestaurantFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantCategoriesAdapter extends RecyclerView.Adapter<RestaurantCategoriesAdapter.RestaurantCategoryViewHolder> {


    private Context context;
    private List<ItemRestaurantData> categoriesList;
    private FragmentManager fragmentManager;



    public RestaurantCategoriesAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<ItemRestaurantData> categoriesList,
                        FragmentManager fragmentManager , ProgressBar progressBar) {
        this.categoriesList = categoriesList;
        this.fragmentManager = fragmentManager;


    }


    @NonNull
    @Override
    public RestaurantCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant_category
                , parent, false);
        return new RestaurantCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantCategoryViewHolder holder, int position) {
        ItemRestaurantData categoryData = categoriesList.get(position);
        String catName = categoryData.getName();
        String imgUrl = categoryData.getPhotoUrl();

        holder.itemRestaurantCategoryTxtViewCatName.setText(catName);
        Glide.with(context).load(imgUrl).into(holder.itemRestaurantCategoryImg);


    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }


    class RestaurantCategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.item_restaurant_category_img_btn_delete)
        ImageButton itemRestaurantCategoryImgBtnDelete;
        @BindView(R.id.item_restaurant_category_img_btn_edit)
        ImageButton itemRestaurantCategoryImgBtnEdit;
        @BindView(R.id.item_restaurant_category_img)
        ImageView itemRestaurantCategoryImg;
        @BindView(R.id.item_restaurant_category_txt_view_cat_name)
        TextView itemRestaurantCategoryTxtViewCatName;
        @BindView(R.id.item_restaurant_category_constraint_container)
        ConstraintLayout itemRestaurantCategoryConstraintContainer;

        private ProgressBar dialogProgressbar;
        private TextView dialogErrorTxtView;
        private TextView dialogTopTxtView;
        private ImageView dialogImg;
        private EditText dialogEdtTxt;
        private Button dialogBtn;
        private ConstraintLayout dialogConstraintContainer;

        private ItemRestaurantData categoryData;
        private ApiService apiService;
        private boolean isCategoryEdited;

        public RestaurantCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        @OnClick({R.id.item_restaurant_category_img_btn_delete, R.id.item_restaurant_category_img_btn_edit, R.id.item_restaurant_category_constraint_container})
        public void onViewClicked(View view) {

            apiService = RetrofitClient.getClient().create(ApiService.class);
            categoryData = categoriesList.get(getAdapterPosition());
            switch (view.getId()) {
                case R.id.item_restaurant_category_img_btn_delete:


                    Utils.showProgressDialog(context,"deleting");
                    apiService.deleteCategory(SharedPreference.loadString(context, SharedPreference.API_TOKEN_KEY),
                            categoryData.getId()).enqueue(new Callback<EditCategory>() {
                        @Override
                        public void onResponse(Call<EditCategory> call, Response<EditCategory> response) {
                            try {
                                if (response.body().getStatus() == 1) {
                                    Utils.dismissProgressDialog();
                                    Toast.makeText(context, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                                   Utils.replaceFragment(fragmentManager,R.id.activity_home_frame,
                                           new HomeRestaurantFragment());


                                } else {
                                    Toast.makeText(context, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<EditCategory> call, Throwable t) {

                            Toast.makeText(context,
                                    context.getText(R.string.default_response_no_internet_connection),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                    break;
                case R.id.item_restaurant_category_img_btn_edit:

                    Dialog editDialog = Utils.dialog(context, R.layout.dialog_category_restaurant);
                    editDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            HomeActivity homeActivity = (HomeActivity) context;
                            homeActivity.getActivityBottomNav().setVisibility(View.VISIBLE);

                            if (isCategoryEdited)
                            Utils.replaceFragment(fragmentManager,R.id.activity_home_frame,
                                    new HomeRestaurantFragment());
                        }
                    });

                    dialogBtn = editDialog.findViewById(R.id.dialog_category_restaurant_btn);
                    dialogConstraintContainer = editDialog.findViewById(R.id.dialog_category_restaurant_constraint_container);
                    dialogProgressbar = editDialog.findViewById(R.id.dialog_category_restaurant_progressbar);
                    dialogEdtTxt = editDialog.findViewById(R.id.dialog_category_restaurant_edt_txt);
                    dialogErrorTxtView = editDialog.findViewById(R.id.dialog_category_restaurant_txt_view_error);
                    dialogTopTxtView = editDialog.findViewById(R.id.dialog_category_restaurant_txt_view_top);
                    dialogImg = editDialog.findViewById(R.id.dialog_category_restaurant_img);

                    dialogBtn.setText(context.getText(R.string.dialog_restaurant_category_btn_modify));
                    dialogTopTxtView.setText(context.getText(R.string.dialog_restaurant_category_top_txt_view_modify_category));
                    dialogEdtTxt.setText(categoryData.getName());
                    Glide.with(context).load(categoryData.getPhotoUrl()).into(dialogImg);

                    dialogImg.setOnClickListener(this);
                    dialogBtn.setOnClickListener(this);


                    break;
                case R.id.item_restaurant_category_constraint_container:

                    int catId = categoryData.getId();
                    HomeRestaurantCategoryFragment fragment = new HomeRestaurantCategoryFragment();
                    HomeRestaurantCategoryFragment.categoryId = catId;
                    HomeRestaurantCategoryFragment.categoryName = categoryData.getName();
                    Utils.replaceFragment(fragmentManager,R.id.activity_home_frame,fragment);
                    break;
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.dialog_category_restaurant_img:
                    Utils.selectImage(context, dialogImg);
                    break;
                case R.id.dialog_category_restaurant_btn:
                    RequestBody name = RequestBody.create(MediaType.parse("text/plain"), dialogEdtTxt.getText().toString());
                    MultipartBody.Part file = Utils.convertFileToMultipart(Utils.path, "photo");
                    Utils.path = null;
                    RequestBody apiToken = RequestBody.create(MediaType.parse("text/plain"),
                            SharedPreference.loadString(context, SharedPreference.API_TOKEN_KEY));
                    RequestBody catId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(categoryData.getId()));

                    Utils.showProgressBar(dialogConstraintContainer, dialogErrorTxtView, dialogProgressbar);

                    apiService.updateCategory(name, file, apiToken, catId).enqueue(new Callback<EditCategory>() {
                        @Override
                        public void onResponse(Call<EditCategory> call, Response<EditCategory> response) {
                            try {
                                if (response.body().getStatus() == 1) {

                                    dialogErrorTxtView.setText(response.body().getMsg());
                                    Utils.showErrorText(dialogConstraintContainer, dialogErrorTxtView, dialogProgressbar);
                                    isCategoryEdited = true;

                                } else {
                                    Utils.showContainer(dialogConstraintContainer, dialogErrorTxtView, dialogProgressbar);
                                    Toast.makeText(context, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<EditCategory> call, Throwable t) {

                            Utils.showContainer(dialogConstraintContainer, dialogErrorTxtView, dialogProgressbar);
                            Toast.makeText(context,
                                    context.getText(R.string.default_response_no_internet_connection),
                                    Toast.LENGTH_SHORT).show();

                        }
                    });
                    break;
            }
        }
    }
}
