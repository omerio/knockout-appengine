/**
 * Customer model
 * @param {Object} customer - a customer
 */
function Customer(customer) {
	ko.mapping.fromJS(customer, {
		'services': {
			create: function (options) {
				return new Service(options.data);
			}
		}
	}, this);
	this.edit = ko.observable(false);

}
/**
 * Service model
 * @param {Object} service - a service
 */
function Service(service) {

	ko.mapping.fromJS(service, {}, this);

	this.edit = ko.observable(false);

	/**
	 * Start editing a service
	 * @param  {Object} service - a service object
	 * @param  {Event} event - DOM event
	 */
	this.startEdit = function (service, event) {
		if (service.stopping) {
			clearTimeout(service.stopping);
		}
		service.edit(true);

		var element = event.target;
		switch (element.tagName) {
		case "SPAN":
			$(element).next().focus();
			break;
		case "INPUT":
			$(element).focus();
			break;
		}
	};

	/**
	 * Stop editing a service
	 * @param  {Object} service - a service object
	 */
	this.stopEdit = function (service) {
		service.stopping = setTimeout(function () {
			service.edit(false);
		}, 200);
	};
}
/**
 * Custom Knockout components
 * A service-credits component
 */
ko.components.register('service-credits', {
	template: {
		//element: 'service-credits-template'
		fromUrl: 'serviceCredits.html'
	},
	viewModel: function (params) {

		this.customer = params.customer;
		this.serviceNames = ['Website Templates', 'Stock Images', 'Sound Tracks', 'Screen Savers', 'Wordpress Themes'];

		this.addService = function () {
			if (!this.customer().services) {
				this.customer().services = ko.observableArray();
			}
			this.customer().services.push(new Service({
				"name": "",
				"credit": "1"
			}));
		};

		/**
		 * Put any jQuery UI initialisation here or any code that
		 * needs to run after the component is rendered
		 * @param {Object} args
		 */
		this.afterRender = function (arg) {
			console.log('service-credits template rendered');
		};
	}
});

/**
 * Customer component template loader
 * @see http://knockoutjs.com/documentation/component-loaders.html
 */
var templateFromUrlLoader = {
	loadTemplate: function (name, templateConfig, callback) {
		if (templateConfig.fromUrl) {
			toastr.info("Loading template");
			// Uses jQuery's ajax facility to load the markup from a file
			var fullUrl = 'templates/' + templateConfig.fromUrl;
			$.get(fullUrl, function (markupString) {
				// We need an array of DOM nodes, not a string.
				// We can use the default loader to convert to the
				// required format.
				ko.components.defaultLoader.loadTemplate(name, markupString, callback);
				toastr.success("Template loaded successfully");
			});
		} else {
			// Unrecognized config format. Let another loader handle it.
			callback(null);
		}
	}
};

// Register it
ko.components.loaders.unshift(templateFromUrlLoader);
/**
 * document onload
 */
$(function () {
	toastr.options.extendedTimeOut = 100; //1000;
	toastr.options.timeOut = 1000;
	toastr.options.fadeOut = 250;
	toastr.options.fadeIn = 250;
	var admin = new CustomerAdmin($('.container')[0]);
	//ko.applyBindings(admin, $('.container')[0]);
	admin.load(true);
});
/**
 * Customer Admin view model
 * @param {Node} element - the DOM node to bind the view model to
 */
function CustomerAdmin(element) {

	// save a reference to this object so that we can use it inside closures and other scopes
	var self = this;

	this.saving = ko.observable(false);

	this.selected = ko.observable(false);

	this.setSelected = function (customer) {
		this.selected(customer);
	};

	this.add = function () {

		var customer = {
			name: ko.observable(''),
			emailAddress: ko.observable(''),
			age: ko.observable(0),
			bio: ko.observable('new customer'),
			edit: ko.observable(true),
			services: ko.observableArray()
		};

		this.customers.push(customer);
	};

	this.remove = function (index) {
		this.customers.splice(index(), 1);
		this.selected(false);
	};

	this.toggleEdit = function (customer, edit) {
		customer.edit(edit);
	};

	this.save = function () {
		var customers = ko.toJSON(this.customers);
		toastr.info("Saving customers");
		this.saving(true);

		$.ajax({
			url: "/data/customers",
			method: "POST",
			data: {
				customers: customers
			}

		}).done(function (status) {

			if (status && status.success) {
				toastr.success("Customers saved successfully");
				self.load(false);

			} else {
				toastr.error("Failed to save customers");
			}
			self.saving(false);

		}).fail(function () {
			toastr.error("Failed to save customers");
			self.saving(false);
		});

	};

	this.load = function (initialLoad) {
		toastr.info("Loading customers");

		$.ajax({
			url: "/data/customers",
			method: "GET"

		}).done(function (customers) {

			var customersArray = ko.mapping.fromJS(customers, {
				create: function (options) {
					return new Customer(options.data);
				}
			});

			if (initialLoad) {
				// initial load
				self.customers = customersArray;

				ko.applyBindings(self, element);
				$(element).removeClass('hidden');

			} else {
				// just a refresh
				self.customers(customersArray());
			}

			self.selected(false);

			toastr.success("Customers loaded successfully");

		}).fail(function () {
			toastr.error("Failed to loaded customers");
		});
	};
}
//# sourceMappingURL=app.js.map