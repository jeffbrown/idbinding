package demo

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class WidgetController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Widget.list(params), model:[widgetCount: Widget.count()]
    }

    def show(Widget widget) {
        respond widget
    }

    def create() {
        respond new Widget(params)
    }

    @Transactional
    def save(Widget widget) {
        if (widget == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (widget.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond widget.errors, view:'create'
            return
        }

        widget.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'widget.label', default: 'Widget'), widget.id])
                redirect widget
            }
            '*' { respond widget, [status: CREATED] }
        }
    }

    def edit(Widget widget) {
        respond widget
    }

    @Transactional
    def update(Widget widget) {
        if (widget == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (widget.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond widget.errors, view:'edit'
            return
        }

        widget.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'widget.label', default: 'Widget'), widget.id])
                redirect widget
            }
            '*'{ respond widget, [status: OK] }
        }
    }

    @Transactional
    def delete(Widget widget) {

        if (widget == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        widget.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'widget.label', default: 'Widget'), widget.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'widget.label', default: 'Widget'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
