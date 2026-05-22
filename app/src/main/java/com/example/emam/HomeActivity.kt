package com.example.emam

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    // ── Pregnancy data ────────────────────────────────────────────────────────
    private val userName    = "Ratna Sari"
    private val gestWeeks   = 18
    private val totalWeeks  = 40
    private val hplDate     = "28 Juli 2026"

    // ── Nutrition data ─────────
    private val caloriesConsumed = 1530
    private val caloriesTarget   = 2000
    private val nutrients = listOf(
        Pair("Zat besi", 58),
        Pair("Folat",    72),
        Pair("Kalsium",  85),
        Pair("Zinc",     45),
        Pair("Yodium",   90),
        Pair("Vit D",    40)
    )

    // ── LILA & Hb data ────────────────────────────────────────────────────────
    // Normal LILA ≥ 23.5 cm (Kemenkes RI 2015)
    // Normal Hb  ≥ 11 gr/dl (WHO)
    private val lilaLabels = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun")
    private val lilaValues = listOf(23.8f, 24.0f, 24.1f, 24.3f, 24.5f, 24.6f)
    private val hbLabels   = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun")
    private val hbValues   = listOf(11.2f, 11.0f, 10.8f, 11.1f, 11.4f, 11.6f)

    // ── Colors ────────────────────────────────────────────────────────────────
    private val colorPrimary   = Color.parseColor("#AF3E4D")
    private val colorSecondary = Color.parseColor("#E4B1AB")
    private val colorAccent    = Color.parseColor("#EAF9D9")
    private val colorGrey      = Color.parseColor("#989898")

    private var showingLila = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scroll = ScrollView(this).apply {
            setBackgroundColor(Color.parseColor("#F8F4F4"))
        }
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val d = resources.displayMetrics.density

        root.addView(buildHeader(d))
        root.addView(buildPregnancyCard(d))
        root.addView(buildAiNote(d))
        root.addView(buildNutritionCard(d))
        root.addView(buildStatusSection(d))

        scroll.addView(root)

        val frame = android.widget.FrameLayout(this)
        val scrollLP = android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT
        )
        frame.addView(scroll, scrollLP)

        val navBar = buildNavBar(d)
        val navLP = android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            (64 * d).toInt(),
            Gravity.BOTTOM
        )
        frame.addView(navBar, navLP)

        root.setPadding(0, 0, 0, (80 * d).toInt())

        setContentView(frame)
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private fun buildHeader(d: Float): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding((16*d).toInt(), (20*d).toInt(), (16*d).toInt(), (8*d).toInt())
        }

        // Avatar placeholder
        val avatar = View(this).apply {
            layoutParams = LinearLayout.LayoutParams((48*d).toInt(), (48*d).toInt()).apply {
                rightMargin = (12*d).toInt()
            }
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(colorSecondary)
            }
        }

        val pill = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(colorAccent)
                cornerRadius = 24f * d
                setStroke((1f*d).toInt(), Color.parseColor("#C8E6C9"))
            }
            setPadding((12*d).toInt(), (8*d).toInt(), (16*d).toInt(), (8*d).toInt())
        }

        val tvGreet = TextView(this).apply {
            text = "Halo, bunda 👋"
            textSize = 11f
            setTextColor(Color.parseColor("#555555"))
        }
        val tvName = TextView(this).apply {
            text = userName
            textSize = 15f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.parseColor("#1A1A1A"))
        }
        pill.addView(tvGreet)
        pill.addView(tvName)

        row.addView(avatar)
        row.addView(pill)
        return row
    }

    // ── Pregnancy card ────────────────────────────────────────────────────────
    private fun buildPregnancyCard(d: Float): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(colorPrimary)
                cornerRadius = 20f * d
            }
            setPadding((16*d).toInt(), (16*d).toInt(), (16*d).toInt(), (16*d).toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                leftMargin  = (16*d).toInt()
                rightMargin = (16*d).toInt()
                bottomMargin= (12*d).toInt()
            }
        }

        // Row: Usia Kehamilan + HPL
        val row1 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = (10*d).toInt() }
            weightSum = 2f
        }

        row1.addView(buildInfoBox(d, "📅 Usia Kehamilan", "$gestWeeks Minggu", 20f))
        row1.addView(buildInfoBox(d, "📅 Perkiraan Lahir", hplDate, 16f, isRight = true))
        card.addView(row1)

        // Trimester box
        val trimBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.parseColor("#55FFFFFF"))
                cornerRadius = 10f * d
            }
            setPadding((12*d).toInt(), (10*d).toInt(), (12*d).toInt(), (10*d).toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val tvTrimLabel = TextView(this).apply {
            text = "📅 Trimester"
            textSize = 11f
            setTextColor(Color.parseColor("#EAF9D9"))
        }
        trimBox.addView(tvTrimLabel)

        // T1 T2 T3 labels row
        val labelsRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = (6*d).toInt(); bottomMargin = (4*d).toInt() }
            weightSum = 3f
        }
        listOf("T1 1-12 Minggu", "T2 13-26 Minggu", "T3 27-40 Minggu").forEachIndexed { i, lbl ->
            labelsRow.addView(TextView(this).apply {
                text = lbl
                textSize = 10f
                setTextColor(Color.parseColor("#EAF9D9"))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                gravity = when(i) { 0 -> Gravity.START; 1 -> Gravity.CENTER; else -> Gravity.END }
            })
        }
        trimBox.addView(labelsRow)

        // Progress bar
        val progress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (10*d).toInt()
            )
            max = 100
            this.progress = ((gestWeeks.toFloat() / totalWeeks) * 100).toInt()
            progressDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.parseColor("#EAF9D9"))
                cornerRadius = 5f * d
            }
        }
        trimBox.addView(progress)

        val tvWeekNote = TextView(this).apply {
            text = "Memasuki Minggu ke-$gestWeeks dari $totalWeeks"
            textSize = 11f
            setTextColor(Color.parseColor("#EAF9D9"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = (6*d).toInt() }
        }
        trimBox.addView(tvWeekNote)
        card.addView(trimBox)

        return card
    }

    private fun buildInfoBox(d: Float, label: String, value: String, valSize: Float, isRight: Boolean = false): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.parseColor("#55FFFFFF"))
                cornerRadius = 10f * d
            }
            setPadding((10*d).toInt(), (10*d).toInt(), (10*d).toInt(), (10*d).toInt())
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                if (isRight) leftMargin = (8*d).toInt() else rightMargin = (8*d).toInt()
            }
            addView(TextView(this@HomeActivity).apply {
                text = label; textSize = 11f; setTextColor(Color.parseColor("#EAF9D9"))
            })
            addView(TextView(this@HomeActivity).apply {
                text = value; textSize = valSize; setTypeface(null, Typeface.BOLD)
                setTextColor(Color.parseColor("#EAF9D9"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = (4*d).toInt() }
            })
        }
    }

    // ── AI Note ───────────────────────────────────────────────────────────────
    private fun buildAiNote(d: Float): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.WHITE)
                cornerRadius = 16f * d
                setStroke((1f*d).toInt(), colorSecondary)
            }
            setPadding((16*d).toInt(), (14*d).toInt(), (16*d).toInt(), (14*d).toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                leftMargin = (16*d).toInt(); rightMargin = (16*d).toInt(); bottomMargin = (12*d).toInt()
            }
        }

        card.addView(TextView(this).apply {
            text = "🍽 Catatan Dari AI Asisten Nutrisi"
            textSize = 13f
            setTypeface(null, Typeface.BOLD)
            setTextColor(colorPrimary)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = (6*d).toInt() }
        })

        card.addView(TextView(this).apply {
            text = "Vit D Kamu Kurang Hari Ini! Baru 40% dari kebutuhan harian. " +
                    "Coba tambahkan hati ayam atau kacang merah di makan malam ya Bunda"
            textSize = 13f
            setTextColor(Color.parseColor("#333333"))
            setLineSpacing(0f, 1.4f)
        })

        return card
    }

    // ── Nutrition summary ─────────────────────────────────────────────────────
    private fun buildNutritionCard(d: Float): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.WHITE)
                cornerRadius = 16f * d
                setStroke((1f*d).toInt(), Color.parseColor("#EEEEEE"))
            }
            setPadding((16*d).toInt(), (16*d).toInt(), (16*d).toInt(), (16*d).toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                leftMargin = (16*d).toInt(); rightMargin = (16*d).toInt(); bottomMargin = (12*d).toInt()
            }
        }

        card.addView(TextView(this).apply {
            text = "🍽 Ringkasan Asupan Nutrisi"
            textSize = 14f; setTypeface(null, Typeface.BOLD)
            setTextColor(Color.parseColor("#1A1A1A"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = (8*d).toInt() }
        })

        card.addView(TextView(this).apply {
            text = "${caloriesConsumed}/${caloriesTarget} kkal"
            textSize = 22f; setTypeface(null, Typeface.BOLD)
            setTextColor(Color.parseColor("#1A1A1A"))
        })

        // Calorie progress bar
        val calBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (10*d).toInt()
            ).apply { topMargin = (4*d).toInt() }
            max = 100
            progress = ((caloriesConsumed.toFloat() / caloriesTarget) * 100).toInt()
        }
        card.addView(calBar)

        val rowSisa = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = (2*d).toInt(); bottomMargin = (12*d).toInt() }
        }
        rowSisa.addView(TextView(this).apply {
            text = "Sisa kalori hari ini"
            textSize = 11f; setTextColor(colorGrey)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        rowSisa.addView(TextView(this).apply {
            text = "${caloriesTarget - caloriesConsumed} kkal lagi"
            textSize = 11f; setTextColor(colorGrey)
        })
        card.addView(rowSisa)

        var currentRow: LinearLayout? = null
        nutrients.forEachIndexed { i, (name, pct) ->
            if (i % 3 == 0) {
                currentRow = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    weightSum = 3f
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { bottomMargin = (8*d).toInt() }
                }
                card.addView(currentRow)
            }
            currentRow?.addView(buildNutrientItem(d, name, pct))
        }

        // Add food button
        val btnAdd = Button(this).apply {
            text = "+ Tambah makanan manual"
            textSize = 14f
            setTextColor(colorPrimary)
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.WHITE)
                cornerRadius = 24f * d
                setStroke((1.5f*d).toInt(), colorPrimary)
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (48*d).toInt()
            ).apply { topMargin = (12*d).toInt() }
            setOnClickListener {
                Toast.makeText(this@HomeActivity, "Tambah makanan manual", Toast.LENGTH_SHORT).show()
            }
        }
        card.addView(btnAdd)

        return card
    }

    private fun buildNutrientItem(d: Float, name: String, pct: Int): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                rightMargin = (8*d).toInt()
            }
            addView(TextView(this@HomeActivity).apply { text = name; textSize = 12f; setTextColor(Color.parseColor("#555555")) })
            addView(TextView(this@HomeActivity).apply {
                text = "$pct%"; textSize = 16f; setTypeface(null, Typeface.BOLD)
                setTextColor(Color.parseColor("#1A1A1A"))
            })
            addView(ProgressBar(this@HomeActivity, null, android.R.attr.progressBarStyleHorizontal).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, (6*d).toInt()
                ).apply { topMargin = (2*d).toInt() }
                max = 100; progress = pct
            })
        }
    }

    // ── Status section (LILA / Hemoglobin) ───────────────────────────────────
    private fun buildStatusSection(d: Float): View {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { leftMargin = (16*d).toInt(); rightMargin = (16*d).toInt() }
        }

        // Tab buttons
        val tabRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            weightSum = 2f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = (8*d).toInt() }
        }

        val btnLila = buildTabBtn(d, "Status LILA", active = true)
        val btnHb   = buildTabBtn(d, "Hemoglobin", active = false)
        tabRow.addView(btnLila)
        tabRow.addView(btnHb)
        container.addView(tabRow)

        // Chart placeholder card
        val chartCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.WHITE)
                cornerRadius = 16f * d
                setStroke((1f*d).toInt(), Color.parseColor("#EEEEEE"))
            }
            setPadding((16*d).toInt(), (16*d).toInt(), (16*d).toInt(), (16*d).toInt())
            id = View.generateViewId()
        }
        container.addView(chartCard)

        fun refreshChart(isLila: Boolean) {
            chartCard.removeAllViews()
            val labels = if (isLila) lilaLabels else hbLabels
            val values = if (isLila) lilaValues else hbValues
            val threshold = if (isLila) 23.5f else 11.0f
            val unit = if (isLila) "cm" else "gr/dl"
            val title = if (isLila) "Status LILA" else "Status Hemoglobin"
            val note = if (isLila)
                "Lingkar Lengan Atas · Batas KEK: < 23.5 cm"
            else
                "Hemoglobin · Normal ibu hamil: ≥ 11 gr/dl"

            val latest = values.last()
            val prev   = values[values.size - 2]
            val delta  = latest - prev
            val isNormal = latest >= threshold

            chartCard.addView(TextView(this).apply {
                text = title; textSize = 15f; setTypeface(null, Typeface.BOLD)
                setTextColor(Color.parseColor("#1A1A1A"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = (4*d).toInt() }
            })

            val valRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = (12*d).toInt() }
            }
            valRow.addView(TextView(this).apply {
                text = "$latest $unit"; textSize = 28f; setTypeface(null, Typeface.BOLD)
                setTextColor(colorPrimary)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { rightMargin = (10*d).toInt() }
            })
            valRow.addView(TextView(this).apply {
                val sign = if (delta >= 0) "↑" else "↓"
                text = "$sign ${String.format("%.1f", Math.abs(delta))} dari bulan lalu"
                textSize = 13f
                setTextColor(if (delta >= 0) Color.parseColor("#2E7D32") else Color.parseColor("#C62828"))
            })
            chartCard.addView(valRow)

            // Status badge
            val statusColor = if (isNormal) Color.parseColor("#2E7D32") else Color.parseColor("#C62828")
            val statusBg    = if (isNormal) Color.parseColor("#E8F5E9") else Color.parseColor("#FFEBEE")
            chartCard.addView(TextView(this).apply {
                text = if (isNormal) "✓ Normal" else "⚠ Perlu Perhatian"
                textSize = 12f; setTypeface(null, Typeface.BOLD)
                setTextColor(statusColor)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    setColor(statusBg); cornerRadius = 8f * d
                }
                setPadding((10*d).toInt(), (4*d).toInt(), (10*d).toInt(), (4*d).toInt())
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = (12*d).toInt() }
            })

            // Simple bar chart
            val chartFrame = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.BOTTOM
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, (120*d).toInt()
                ).apply { bottomMargin = (4*d).toInt() }
                weightSum = labels.size.toFloat()
            }
            val maxVal = values.max()
            val minVal = (values.min() - 1f).coerceAtLeast(0f)
            val range  = (maxVal - minVal).coerceAtLeast(1f)

            values.forEachIndexed { i, v ->
                val barContainer = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                }
                val barPct   = ((v - minVal) / range).coerceIn(0.05f, 1f)
                val barColor = if (v >= threshold) colorPrimary else Color.parseColor("#E53935")
                val isLatest = i == values.size - 1

                val bar = View(this).apply {
                    background = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        setColor(if (isLatest) barColor else Color.parseColor("#E4B1AB"))
                        cornerRadii = floatArrayOf(4f*d,4f*d,4f*d,4f*d,0f,0f,0f,0f)
                    }
                    layoutParams = LinearLayout.LayoutParams(
                        (20*d).toInt(), (barPct * 100 * d).toInt()
                    )
                }
                barContainer.addView(bar)

                barContainer.addView(TextView(this).apply {
                    text = labels[i]; textSize = 9f
                    setTextColor(colorGrey); gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { topMargin = (4*d).toInt() }
                })
                chartFrame.addView(barContainer)
            }
            chartCard.addView(chartFrame)

            // Threshold line note
            chartCard.addView(View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, (1f*d).toInt()
                ).apply { topMargin = (4*d).toInt(); bottomMargin = (4*d).toInt() }
                setBackgroundColor(Color.parseColor("#EEEEEE"))
            })

            chartCard.addView(TextView(this).apply {
                text = note; textSize = 11f; setTextColor(colorGrey)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = (4*d).toInt() }
            })
        }

        // Tab click handlers
        btnLila.setOnClickListener {
            activateTab(btnLila, d); deactivateTab(btnHb, d)
            refreshChart(true)
        }
        btnHb.setOnClickListener {
            activateTab(btnHb, d); deactivateTab(btnLila, d)
            refreshChart(false)
        }

        refreshChart(true)
        return container
    }

    private fun buildTabBtn(d: Float, label: String, active: Boolean): Button {
        return Button(this).apply {
            text = label; textSize = 13f
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 20f * d
                if (active) setColor(colorPrimary) else {
                    setColor(Color.WHITE)
                    setStroke((1.5f*d).toInt(), colorPrimary)
                }
            }
            setTextColor(if (active) Color.WHITE else colorPrimary)
            layoutParams = LinearLayout.LayoutParams(0, (40*d).toInt(), 1f).apply {
                rightMargin = (4*d).toInt(); leftMargin = (4*d).toInt()
            }
        }
    }

    private fun activateTab(btn: Button, d: Float) {
        btn.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 20f * d
            setColor(colorPrimary)
        }
        btn.setTextColor(Color.WHITE)
    }

    private fun deactivateTab(btn: Button, d: Float) {
        btn.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 20f * d
            setColor(Color.WHITE)
            setStroke((1.5f*d).toInt(), colorPrimary)
        }
        btn.setTextColor(colorPrimary)
    }

    // ── Bottom navigation ─────────────────────────────────────────────────────
    private fun buildNavBar(d: Float): LinearLayout {
        val nav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            weightSum = 5f
            setBackgroundColor(Color.WHITE)
            elevation = 12f * d
        }

        fun navItem(icon: String, label: String, active: Boolean, onClick: () -> Unit): LinearLayout {
            return LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                isClickable = true; isFocusable = true
                addView(TextView(this@HomeActivity).apply {
                    text = icon; textSize = 20f; gravity = Gravity.CENTER
                    setTextColor(if (active) colorPrimary else colorGrey)
                })
                addView(TextView(this@HomeActivity).apply {
                    text = label; textSize = 10f; gravity = Gravity.CENTER
                    setTextColor(if (active) colorPrimary else colorGrey)
                    if (active) setTypeface(null, Typeface.BOLD)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { topMargin = (2*d).toInt() }
                })
                setOnClickListener { onClick() }
            }
        }

        nav.addView(navItem("🏠", "Home", true) {
            Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
        })
        nav.addView(navItem("💬", "Tanya AI", false) {
            startActivity(Intent(this, ChatActivity::class.java))
        })
        nav.addView(navItem("📷", "Scan", false) {
            startActivity(Intent(this, MainActivity::class.java))
        })
        nav.addView(navItem("👥", "Forum", false) {
            startActivity(Intent(this, ForumActivity::class.java))
        })
        nav.addView(navItem("👤", "User", false) {
            Toast.makeText(this, "Profile coming soon!", Toast.LENGTH_SHORT).show()
        })

        return nav
    }
}