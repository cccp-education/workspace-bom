package contracts.runtime

interface InteractionProtocol {
    fun nextTurn(current: FormationTurn): FormationTurn?
}