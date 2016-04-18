package demo

import grails.test.mixin.*
import spock.lang.*

@TestFor(WidgetController)
@Mock(Widget)
class WidgetControllerSpec extends Specification {

    def populateValidParams(params) {
        assert params != null
        params.name = 'Some Valid Name'
    }

    void "Test the index action returns the correct model"() {

        when:"The index action is executed"
            controller.index()

        then:"The model is correct"
            !model.widgetList
            model.widgetCount == 0
    }

    void "Test the create action returns the correct model"() {
        when:"The create action is executed"
            controller.create()

        then:"The model is correctly created"
            model.widget!= null
    }

    void "Test the save action correctly persists an instance"() {

        when:"The save action is executed with an invalid instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'POST'
            def widget = new Widget()
            widget.validate()
            controller.save(widget)

        then:"The create view is rendered again with the correct model"
            model.widget!= null
            view == 'create'

        when:"The save action is executed with a valid instance"
            response.reset()
            populateValidParams(params)
            widget = new Widget(params)

            controller.save(widget)

        then:"A redirect is issued to the show action"
            response.redirectedUrl == '/widget/show/1'
            controller.flash.message != null
            Widget.count() == 1
    }

    void "Test that the show action returns the correct model"() {
        when:"The show action is executed with a null domain"
            controller.show(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the show action"
            populateValidParams(params)
            def widget = new Widget(params)
            controller.show(widget)

        then:"A model is populated containing the domain instance"
            model.widget == widget
    }

    void "Test that the edit action returns the correct model"() {
        when:"The edit action is executed with a null domain"
            controller.edit(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the edit action"
            populateValidParams(params)
            def widget = new Widget(params)
            controller.edit(widget)

        then:"A model is populated containing the domain instance"
            model.widget == widget
    }

    void "Test the update action performs an update on a valid domain instance"() {
        when:"Update is called for a domain instance that doesn't exist"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'PUT'
            controller.update(null)

        then:"A 404 error is returned"
            response.redirectedUrl == '/widget/index'
            flash.message != null

        when:"An invalid domain instance is passed to the update action"
            response.reset()
            def widget = new Widget()
            widget.validate()
            controller.update(widget)

        then:"The edit view is rendered again with the invalid instance"
            view == 'edit'
            model.widget == widget

        when:"A valid domain instance is passed to the update action"
            response.reset()
            populateValidParams(params)
            widget = new Widget(params).save(flush: true)
            controller.update(widget)

        then:"A redirect is issued to the show action"
            widget != null
            response.redirectedUrl == "/widget/show/$widget.id"
            flash.message != null
    }

    void "Test that the delete action deletes an instance if it exists"() {
        when:"The delete action is called for a null instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'DELETE'
            controller.delete(null)

        then:"A 404 is returned"
            response.redirectedUrl == '/widget/index'
            flash.message != null

        when:"A domain instance is created"
            response.reset()
            populateValidParams(params)
            def widget = new Widget(params).save(flush: true)

        then:"It exists"
            Widget.count() == 1

        when:"The domain instance is passed to the delete action"
            controller.delete(widget)

        then:"The instance is deleted"
            Widget.count() == 0
            response.redirectedUrl == '/widget/index'
            flash.message != null
    }
}
