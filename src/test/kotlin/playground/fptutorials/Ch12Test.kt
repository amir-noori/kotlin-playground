package playground.fptutorials

import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import kotlin.random.Random


/**
 *
 *   Property Based Testing
 *
 */

class PropertyTestTest {

    @Test
    fun `sum test using predefined values`() {
        assertThat("2 + 3 = 5", sum(2, 3) == 5)
        assertThat("2 + 5 = 7", sum(2, 5) == 7)
    }

    @Test
    fun `sum test using random values`() {
        repeat(100) {
            val firstValue = Random.nextInt()
            val secondValue = Random.nextInt()
            val expectedValue = firstValue + secondValue
            assertThat(
                "$firstValue + $secondValue = $expectedValue",
                sum(firstValue, secondValue) == expectedValue
            )
        }
    }

    @Test
    fun `sum is commutative`() {
        repeat(100) {
            val firstValue = Random.nextInt()
            val secondValue = Random.nextInt()
            assertThat(
                "$firstValue + $secondValue = $secondValue + $firstValue",
                sum(firstValue, secondValue) == sum(secondValue, firstValue)
            )
        }
    }

    @Test
    fun `sum is not multiplication`() {
        val constant = Random.nextInt()
        repeat(100) {
            val value = Random.nextInt()
            assertThat(
                "value + const + const = value + 2 * const",
                sum(sum(value, constant), constant) == sum(value, 2 * constant)
            )
        }
    }

    @Test
    fun `test sum unit value (0)`() {
        repeat(100) {
            val value = Random.nextInt()
            assertThat(
                "value + 0 = value",
                sum(value, 0) == value
            )
        }
    }

    @Test
    fun `Property-based test for sum`() {
        repeat(100) {
            val additionProp = CommutativeProperty<Int>() and
                    AssociativeProperty() and
                    IdentityProperty(0)
            val evaluation = additionProp(IntGenerator) {
                sum(it[0], it[1])
            }
            assertThat("", evaluation)
        }
    }


}





