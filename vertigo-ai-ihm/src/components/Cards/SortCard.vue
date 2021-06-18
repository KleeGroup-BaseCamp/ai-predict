<template>
  <q-item>
    <div class="bg-white overview-card">
      <div class="row">
        <div class="text-h6 text-primary">Sort</div>
      </div>
      <div class="row overview-items">
      <q-select
        borderless
        class="col"
        type="text"
        v-model="sortForm.sort"
        :options="options"
      />
      <q-btn flat color="primary">
        <q-icon
              v-if="sortForm.order=='DESC'"
              name="expand_less"
              class="text"
              style="font-size: 1.5em;"
              @click="changeOrder()"
            />
        <q-icon
              v-else
              name="expand_more"
              class="text"
              style="font-size: 1.5em;"
              @click="changeOrder()"
            />
      </q-btn>
      </div>
    </div>
  </q-item>
</template>

<script>
export default {
  name: "SortCard",
  data() {
    return {
      sortForm: { sort: "", order: "ASC" },
      options: ["Performance", "Use"]
    };
  },
  methods: { 
    changeOrder() {
      if (this.sortForm.order=="ASC"){
        this.sortForm.order="DESC";
      } else {
        this.sortForm.order="ASC";
      }
    }
  },
  watch: {
    sortForm: {
      handler: function() {
        this.$emit("sort", this.sortForm);
      },
      deep: true
    }
  }
};
</script>
