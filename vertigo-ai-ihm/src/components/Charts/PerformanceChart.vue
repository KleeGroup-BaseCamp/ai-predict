<template>
  <div id="chart">
    <apexchart
      type="radialBar"
      :options="chartOptions()"
      :series="[value]"
    ></apexchart>
  </div>
</template>

<script>
import "apexcharts";

export default {
  name: "PerformanceChart",
  props: {
    value: {
      type: Number,
      required: true
    }
  },

  methods: {
    color() {
      if (this.value > 66) {
        return ["#4CAF50"];
      }
      if (this.value < 33) {
        return ["#CC3344"];
      }
      return ["#FFBF00"];
    },

    chartOptions() {
      return {
        chart: {
          type: "radialBar",
          offsetY: -2,
          sparkline: {
            enabled: true
          }
        },
        plotOptions: {
          radialBar: {
            startAngle: -90,
            endAngle: 90,
            track: {
              background: "#e7e7e7",
              strokeWidth: "100%",
              margin: 5 // margin is in pixels
            },
            dataLabels: {
              name: {
                show: false
              },
              value: {
                offsetY: -2,
                fontSize: "20px"
              }
            }
          }
        },
        grid: {
          padding: {
            top: -10
          }
        },
        colors: this.color(),
        fill: {
          type: "flat"
        }
      };
    }
  },

  data: () => ({
    series: [10]
  })
};
</script>
