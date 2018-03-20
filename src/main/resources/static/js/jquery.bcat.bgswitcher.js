/**
 * bcat BG Switcher - unobtrusive background image switcher
 * @version 1.2.0
 * @jQuery version 1.2+
 * @author Yuriy Davats http://www.bcat.eu
 * @copyright Yuriy Davats
 */

;
(function($, window, document, undefined) {

    // Default options
    var pluginName = "bcatBGSwitcher",
        defaults = {
            urls: [], // urls array, should contain at least one image url
            startIndex: 0, // first image loaded
            timeout: 12000, // time between image changes
            alt: 'Picture', // alt for consistency
            speed: 1000, // animation speed
            links: false // generate a link for each image
        };

    // Plugin constructor
    function Plugin(element, options) {

        this._defaults = defaults;
        this._name = pluginName;

        this.init(element, options);
    }

    Plugin.prototype = {
        init: function(element, options) {
            var instance = {};
            instance.currentIndex = options.startIndex;
            instance.currentImage = this.preloadImage(element, options, instance.currentIndex);
            // fix scope
            var that = this;
            // append image on load and start the slide show
            instance.currentImage.load(function() {
                instance.currentImage.appendTo(element);
                instance.currentImage.fadeIn(options.speed);
                instance.currentIndex++;
                if (options.urls[instance.currentIndex]) {
                    if (options.links) {
                        var loaderDiv = $('<div />').attr({
                            'id': element.id + '-loader',
                            'style': 'display: none;'
                        });
                        loaderDiv.appendTo(element);
                        that.generateLinks(element, options, instance);
                    }
                    that.runSlideShow(element, options, instance);
                }
            });
        },
        runSlideShow: function(element, options, instance) {
            // fix scope
            var that = this;
            // update image periodically
            instance.intervalId = setInterval(function() {
                that.updateImage(element, options, instance);
            }, options.timeout);
        },
        updateImage: function(element, options, instance) {
            // load an image and add it to DOM or show the new one and hide the previous one

            // set index to 0 at the end of array
            if (!options.urls[instance.currentIndex]) {
                instance.currentIndex = 0;
            }

            var nextImage = $('#' + element.id + instance.currentIndex);
            if (nextImage.length) {
                // image found in DOM, changing visibility
                instance.currentImage.fadeOut(options.speed);
                nextImage.fadeIn(options.speed);
            } else {
                // image was not loaded yet, loading and showing it
                nextImage = this.preloadImage(element, options, instance.currentIndex);
                this.swapPreloadedImages(instance.currentImage, nextImage, element, options);
            }
            if (options.links) {
                this.setActiveLink(element, options, instance);
            }
            instance.currentImage = nextImage;
            instance.currentIndex++;
        },
        preloadImage: function(element, options, index, style) {
            // preload image and return it as a jQuery object
            if (!style) {
                style = 'display: none;';
            }

            var img = $('<img />');
            img.attr({
                'id': element.id + index,
                'src': options.urls[index],
                'alt': options.alt,
                'style': style
            });
            return img;
        },
        swapPreloadedImages: function(currentImage, nextImage, element, options, showLoader) {
            // swap images on load

            if (showLoader) {
                // show loader
                $(element).addClass('loading');
            }

            nextImage.load(function() {
                if (showLoader) {
                    // hide loader
                    $(element).removeClass('loading');
                }
                nextImage.appendTo(element);
                currentImage.fadeOut(options.speed);
                nextImage.fadeIn(options.speed);
            });
        },
        generateLinks: function(element, options, instance) {
            // generate image links on load
            instance.linkParent = $('<div />');
            instance.linkParent.attr({
                'id': element.id + '-navigation',
                'style': 'display: none;'
            });

            // fix scope
            var that = this;

            $.each(options.urls, function(index, value) {
                var link = $('<a />');
                var linkClass = '';
                if (index === options.startIndex) {
                    linkClass = 'active';
                }
                link.attr({
                    'id': element.id + '-link' + index,
                    'href': '#',
                    'class': linkClass
                });
                link.click(function(event) {
                    event.preventDefault();
                    that.switchImageTo(element, options, instance, index);
                });
                instance.linkParent.append(link);
            });

            instance.linkParent.insertAfter(element);
            instance.linkParent.fadeIn(options.speed);

        },
        switchImageTo: function(element, options, instance, index) {
            // switch to the given image using array index and reset slideshow

            if (!options.urls[index]) {
                console.log('can not switch to a non-existent element');
                return;
            }

            var nextImage = $('#' + element.id + index);

            // prevent action on active image
            if (nextImage.attr('id') !== instance.currentImage.attr('id')) {

                instance.currentIndex = index;

                // stop slide show
                clearInterval(instance.intervalId);

                if (nextImage.length) {
                    // image found in DOM, changing visibility
                    instance.currentImage.fadeOut(options.speed);
                    nextImage.fadeIn(options.speed);
                } else {
                    // image was not loaded yet, loading and showing it
                    nextImage = this.preloadImage(element, options, instance.currentIndex);
                    this.swapPreloadedImages(instance.currentImage, nextImage, element, options, true);
                }

                this.setActiveLink(element, options, instance);
                instance.currentImage = nextImage;
                instance.currentIndex++;

                // fix scope
                var that = this;
                // run slideshow again
                instance.intervalId = setInterval(function() {
                    that.updateImage(element, options, instance);
                }, options.timeout);

            }

        },
        setActiveLink: function(element, options, instance) {
            // set active class to the currently active link
            if (instance.linkParent.length) {
                instance.linkParent.find('a').removeClass('active');
                instance.linkParent.find('a#' + element.id + '-link' + instance.currentIndex).addClass('active');
            }
        }
    };

    // Plugin wrapper preventing against multiple instantiations
    $.fn[pluginName] = function(options) {
        return this.each(function() {
            if (!$.data(this, "plugin_" + pluginName)) {
                var localOptions = $.extend({}, defaults, options);
                $.data(this, "plugin_" + pluginName, new Plugin(this, localOptions));

            }
        });
    };

})(jQuery, window, document);