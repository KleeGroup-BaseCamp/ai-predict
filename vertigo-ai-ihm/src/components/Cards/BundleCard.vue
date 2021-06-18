<template>
  <q-item>
    <q-card
      bordered
      class="bg-white-9 bundle-card q-pa-md"
      @mouseover="hover = true"
      @mouseleave="hover = false"
    >
      <q-card-section class="row">
        <div class="text-h6 text-primary col-8">{{ title }}</div>
        <div class="col-4 row items-center">
          <q-btn flat color="primary" to="/start/pick-quasar-flavour">
            <q-icon
              v-if="hover"
              name="login"
              class="text"
              style="font-size: 1.5em; max-width: 5px;"
            />
            <q-tooltip>
              Explore
            </q-tooltip>
          </q-btn>
          <q-btn flat color="primary" to="/start/pick-quasar-flavour">
            <q-icon
              v-if="hover"
              name="o_file_download"
              class="text"
              style="font-size: 1.5em; max-width: 5px;"
            />
            <q-tooltip>
              Download
            </q-tooltip>
          </q-btn>
          <q-btn flat color="primary" to="/start/pick-quasar-flavour">
            <q-icon
              v-if="hover"
              name="clear"
              class="text"
              style="font-size: 1.5em; max-width: 5px;"
            />
            <q-tooltip>
              Delete
            </q-tooltip>
          </q-btn>
        </div>
      </q-card-section>

      <q-separator color="accent" inset />

      <q-card-section class="row bundle-items">
        <div class="col column items-center">
          <div class="text-h6 text-primary">Performance</div>
          <div>
            <performance-chart v-bind:value="performance"></performance-chart>
          </div>
        </div>
        <div class="col column items-center">
          <div class="text-h6 text-primary">Active</div>
          <div v-if="active" class="">
            <q-icon
              name="check_circle"
              class="text-positive"
              style="font-size: 3em;"
            />
          </div>
          <div v-else>
            <q-icon
              name="cancel"
              class="text-negative"
              style="font-size: 3em;"
            />
          </div>
        </div>
        <div class="col column items-center">
          <div class="text-h6 text-primary">Use</div>
          <div>
            <div class="text-h6 text-dark">{{ use }}</div>
          </div>
        </div>
      </q-card-section>
    </q-card>
  </q-item>
</template>

<script>
import PerformanceChart from "../Charts/PerformanceChart";

export default {
  name: "BundleCard",
  components: { PerformanceChart },
  props: {
    title: {
      type: String,
      required: true
    },

    performance: {
      type: Number,
      required: true
    },

    active: {
      type: Boolean,
      required: true
    },

    use: {
      type: Number,
      required: true
    }
  },
  data() {
    return { hover: false };
  },
  methods: {
    select($event) {
      console.log("select");
    }
  }
};
</script>
