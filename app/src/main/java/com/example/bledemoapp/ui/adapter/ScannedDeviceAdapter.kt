package com.example.bledemoapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.bledemoapp.R
import com.example.bledemoapp.databinding.ItemFindDevicesBinding
import com.example.bledemoapp.model.BaseScannedDevice

class ScannedDeviceAdapter(context: Context) :
    RecyclerView.Adapter<ScannedDeviceAdapter.MyViewHolder>() {

    private var deviceList: ArrayList<BaseScannedDevice> = ArrayList()
    private var context: Context = context
    var animLastPosition = -1

    class MyViewHolder internal constructor(b: ItemFindDevicesBinding) :
        RecyclerView.ViewHolder(b.root) {
        internal var binding: ItemFindDevicesBinding = b
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val li = LayoutInflater.from(context)
        return MyViewHolder(
            ItemFindDevicesBinding.inflate(li)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemData = deviceList[holder.adapterPosition]
        holder.binding.textDeviceName.text = context.getString(R.string.device_name, if (itemData.displayName?.isEmpty() == true) context.getString(R.string.device_name_not_found) else itemData.displayName)
        holder.binding.textMacAddress.text = context.getString(R.string.device_id, itemData.uid)
        holder.itemView.setOnClickListener {}
        playAnimation(position, holder.binding)
    }

    /**
     * Play animation
     * @param position [Int]
     * @param binding [ItemFindDevicesBinding]
     */
    private fun playAnimation(position: Int, binding: ItemFindDevicesBinding) {
        if (position > animLastPosition) {
            val animation: Animation =
                AnimationUtils.loadAnimation(context, R.anim.list_item_slide_in)
            binding.relativeItemView.startAnimation(animation)
            animLastPosition = position
        }
    }

    override fun getItemCount(): Int {
        return deviceList.size
    }

    fun updateDeviceList(bleDeviceList: java.util.ArrayList<BaseScannedDevice>?) {
        deviceList.clear()
        bleDeviceList?.let { deviceList.addAll(it) }
        notifyDataSetChanged()
    }
}