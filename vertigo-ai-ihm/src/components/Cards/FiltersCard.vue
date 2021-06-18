<template>
  <q-item>
    <div class="bg-white overview-card">
      <div class="row">
        <div class="text-h6 text-primary">Filters</div>
      </div>
      <div class="overview-items">
        <div class="filter-items">
          <div class="text-h6 text-primary">Performance</div>
          <div style=" mar">
            <q-badge color="white" class="text-primary">
              Range: {{ filterForm.performance.min }} to
              {{ filterForm.performance.max }} (0 to 100)
            </q-badge>
            <q-range
              v-model="filterForm.performance"
              :min="0"
              :max="100"
              color="accent"
            />
          </div>
        </div>
        <div class="filter-items">
          <div class="text-h6 text-primary">Active</div>
          <div class="row flex-center">
            <div class=" items-center" style="margin-right: 10px">
              <q-icon
                name="check_circle"
                class="text-positive"
                style="font-size: 2em;"
              />
              <q-checkbox v-model="filterForm.active" />
            </div>
            <div class="items-center" style="margin-left: 10px">
              <q-icon
                name="cancel"
                class="text-negative"
                style="font-size: 2em;"
              />
              <q-checkbox v-model="filterForm.deactive" />
            </div>
          </div>
        </div>
        <div class="filter-items">
          <div class="text-h6 text-primary">Use</div>
          <div style=" mar">
            <q-badge color="white" class="text-primary">
              Range: {{ filterForm.use.min }} to {{ filterForm.use.max }} ({{
                0
              }}
              to {{ 100 }})
            </q-badge>
            <q-range
              v-model="filterForm.use"
              :min="0"
              :max="100"
              color="accent"
            />
          </div>
        </div>
      </div>
    </div>
  </q-item>
</template>

<script>
export default {
  name: "FiltersCard",
  data() {
    return {
      filterForm: {
        active: true,
        deactive: true,
        performance: { min: 0, max: 100 },
        use: { min: 0, max: 100 }
      }
    };
  },
  watch: {
    filterForm: {
      handler: function() {
        this.$emit("filters", this.filterForm);
      },
      deep: true
    }
  }
};
</script>
