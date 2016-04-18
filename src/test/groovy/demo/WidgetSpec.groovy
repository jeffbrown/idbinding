package demo

import grails.test.mixin.TestFor
import spock.lang.Specification


@TestFor(Widget)
class WidgetSpec extends Specification {

    void "test id is not bindable"() {
        when:
        def w = new Widget(id: 42, name: 'Some Widget')

        then:
        w.id == null
        w.name == 'Some Widget'
    }
}
