package devtope.compound.microscope.des

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import devtope.compound.microscope.R
import devtope.compound.microscope.databinding.MicroscopeCustomBinding

class DescriptionAdapter(val onItemClick: OnDesClickListener?) :
    RecyclerView.Adapter<DescriptionAdapter.ViewHolder>() {

    private val partsTitle = arrayOf(
        "Eyepiece (ocular lens) with or without Pointer",
        "Monocular or Binocular Head",
        "Arm",
        "Nosepiece",
        "Base",
        "Objective lenses",
        "Specimen or slide",
        "Stage or Platform",
        "Stage clips or mechanical stage",
        "Aperture - Disc or Iris Diaphragm",
        "Abbe Condenser",
        "Coarse and fine adjustment controls",
        "Stage height adjustment",
        "Mirror",
        "Illumination",
        "Bottom Lens or Field Diaphragm",
    )

    private var partsContent = intArrayOf(
        R.array.eye_piece,
        R.array.mono_bino,
        R.array.arm,
        R.array.nose_piece,
        R.array.base,
        R.array.objective_lens,
        R.array.specimen,
        R.array.stage,
        R.array.stage_clips,
        R.array.aperture,
        R.array.abbe,
        R.array.coarse,
        R.array.stage_height,
        R.array.mirror,
        R.array.illumination,
        R.array.bottom_len,
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DescriptionAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MicroscopeCustomBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return partsTitle.size
    }

    override fun onBindViewHolder(holder: DescriptionAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(private val binding: MicroscopeCustomBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.partsName.text = partsTitle[position]
            binding.root.setOnClickListener {
                onItemClick?.OnContentDescClicked(partsTitle[position], partsContent[position])
            }
        }
    }
}