<template>
  <q-page class="row q-pa-sm">
    <section class="col-auto column q-pa-sm">
        <OverviewCard :count="count()" :active="countActive()" />
        <SortCard @sort="getSort($event)" />
        <FiltersCard @filters="getFilters($event)" />
    </section>
    <section class="col row justify-between q-pa-md">
      <div v-for="bundle in bundles" :key="bundle.name">
        <BundleCard
          :title="bundle.name"
          :active="bundle.active"
          v-bind:performance="bundle.performance"
          v-bind:use="bundle.use"
          @deletedBundle="deleteBundle"
        />
      </div>
    </section>
  </q-page>
</template>

<script>
import BundleCard from "../components/Cards/BundleCard.vue";
import axios from "axios";
import OverviewCard from "../components/Cards/OverviewCard.vue";
import FiltersCard from "src/components/Cards/FiltersCard.vue";
import SortCard from "src/components/Cards/SortCard.vue";

export default {
  components: { BundleCard, OverviewCard, FiltersCard, SortCard },
  name: "Overview",
  data() {
    return {
      input_bundles: [],
      bundles: [],
      sort: {},
      filters: {}
    };
  },
  mounted() {
    axios.get("/bundles/").then(response => {
      this.input_bundles = response.data;
      this.bundles = response.data;
    });
  },
  methods: {
    count() {
      return this.bundles.length;
    },

    countActive() {
      var count = 0;
      for (var i = 0; i < this.bundles.length; i++) {
        if (this.bundles[i].active) {
          count += 1;
        }
      }
      return count;
    },
    getSort(e) {
      this.sort = e;
      if (this.sort.sort == "Performance") {
        if (this.sort.order == "ASC") {
          this.bundles.sort((a, b) => (a.performance > b.performance ? 1 : -1));
        } else {
          this.bundles.sort((a, b) => (a.performance > b.performance ? -1 : 1));
        }
      }
      if (this.sort.sort == "Use") {
        if (this.sort.order == "ASC") {
          this.bundles.sort((a, b) => (a.use > b.use ? 1 : -1));
        } else {
          this.bundles.sort((a, b) => (a.use > b.use ? -1 : 1));
        }
      }
    },
    getFilters(e) {
      this.filter = e;
      this.bundles = this.input_bundles.filter(function(bundle) {
        const filters = e;
        const numericFilters =
          bundle.performance > filters.performance.min &&
          bundle.performance < filters.performance.max &&
          bundle.use > filters.use.min &&
          bundle.use < filters.use.max;
        if (
          (filters.active && filters.deactive) ||
          (!filters.active && !filters.deactive)
        ) {
          return numericFilters;
        } else if (filters.active) {
          return numericFilters && bundle.active;
        } else if (filters.deactive) {
          return numericFilters && !bundle.active;
        }
      });
    },
    deleteBundle(e) {
      this.input_bundles = this.input_bundles.filter(function(bundle) {
        return bundle.name != e;
      });
      this.bundles = this.bundles.filter(function(bundle) {
        return bundle.name != e;
      });
      this.deleteAllNotifySuccess(e);
    },
    deleteAllNotifySuccess(name) {
      this.$q.notify({
        message: "All version of".concat(" ", name).concat(" ", "are deleted."),
        color: "positive"
      });
    }
  }
};
</script>
