package com.practica.florideasapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import Models.Cart;
import Models.Productos;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<Productos> listaProductos;
    private List<Productos> productosListFull;


    public ProductoAdapter(Context context, List<Productos> listaProductos) {
        this.context = context;
        this.listaProductos = listaProductos;
        this.productosListFull = new ArrayList<>(listaProductos);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Productos producto = listaProductos.get(position);

        holder.nombreProducto.setText(producto.getNombre());
        holder.precioProducto.setText(String.format("S/ %s", producto.getPrecio()));
        holder.stockProducto.setText("Stock: " + producto.getStock());
        holder.descripcionProducto.setText(producto.getDescripcion());

        Glide.with(context)
                .load(producto.getRutaImagen())
                .into(holder.imagenProducto);

        holder.btnAddCarrito.setOnClickListener(v -> {
            Cart.getInstance().addItem(producto); // Añadir al carrito
            Toast.makeText(context, producto.getNombre() + " añadido al carrito", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    @Override
    public Filter getFilter() {
        return productosFilter;
    }

    private Filter productosFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Productos> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(productosListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Productos item : productosListFull) {
                    if (item.getNombre().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listaProductos.clear();
            listaProductos.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombreProducto, precioProducto, stockProducto, descripcionProducto;
        ImageView imagenProducto;
        Button btnAddCarrito;

        public ViewHolder(View itemView) {
            super(itemView);
            nombreProducto = itemView.findViewById(R.id.productName);
            precioProducto = itemView.findViewById(R.id.productPrice);
            stockProducto = itemView.findViewById(R.id.productStock);
            descripcionProducto = itemView.findViewById(R.id.productDescription);
            imagenProducto = itemView.findViewById(R.id.productImage);
            btnAddCarrito = itemView.findViewById(R.id.btnAddCarrito); // Inicializar el botón
        }
    }
}