package kashyap.anurag.frenzystore.Adapters;

import android.widget.Filter;

import java.util.ArrayList;

import kashyap.anurag.frenzystore.Models.ModelSearchProduct;

public class FilterProducts extends Filter {

    ArrayList<ModelSearchProduct> filterList;
    AdapterSearchProducts adapterSearchProducts;

    public FilterProducts(ArrayList<ModelSearchProduct> filterList, AdapterSearchProducts adapterSearchProducts) {
        this.filterList = filterList;
        this.adapterSearchProducts = adapterSearchProducts;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if (charSequence != null && charSequence.length()>0){
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelSearchProduct> filteredModels = new ArrayList<>();
            for (int i=0; i< filterList.size(); i++){
                if (filterList.get(i).getProductTitle().toUpperCase().contains(charSequence)){
                    filteredModels.add(filterList.get(i));
                }
            }

            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else{
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;

    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapterSearchProducts.searchProductArrayList = (ArrayList<ModelSearchProduct>)filterResults.values;

        adapterSearchProducts.notifyDataSetChanged();
    }
}
