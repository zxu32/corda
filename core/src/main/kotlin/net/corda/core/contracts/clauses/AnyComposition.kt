package net.corda.core.contracts.clauses

import net.corda.core.contracts.AuthenticatedObject
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.TransactionForContract
import java.util.*

/**
 * Compose a number of clauses, such that any number of the clauses can run.
 */
// TODO: Rename to AnyOf
class AnyComposition<in S : ContractState, C : CommandData, in K : Any>(vararg rawClauses: Clause<S, C, K>) : CompositeClause<S, C, K>() {
    override val clauses: List<Clause<S, C, K>> = rawClauses.asList()

    override fun matchedClauses(commands: List<AuthenticatedObject<C>>): List<Clause<S, C, K>> = clauses.filter { it.matches(commands) }

    override fun verify(tx: TransactionForContract, inputs: List<S>, outputs: List<S>, commands: List<AuthenticatedObject<C>>, groupingKey: K?): Set<C> {
        return matchedClauses(commands).flatMapTo(HashSet<C>()) { clause ->
            clause.verify(tx, inputs, outputs, commands, groupingKey)
        }
    }

    override fun toString(): String = "Or: ${clauses.toList()}"
}
